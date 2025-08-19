package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.MessageRequest;
import com.uyarberk.freelance_platformm.dto.MessageResponse;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.Chat;
import com.uyarberk.freelance_platformm.model.Message;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.ChatRepository;
import com.uyarberk.freelance_platformm.repository.MessageRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        log.info("Sending message to chat: {} from user: {}", request.getChatId(), senderId);

        // Kullanıcıyı getir
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı. ID: " + senderId));

        // Chat'i getir ve erişim kontrolü
        Chat chat = chatRepository.findByIdAndUserId(request.getChatId(), senderId)
                .orElseThrow(() -> new RuntimeException("Chat bulunamadı veya erişim yetkiniz yok"));

        // Mesaj oluştur
        Message message = new Message();
        message.setContent(request.getContent());
        message.setChat(chat);
        message.setSender(sender);
        message.setMessageType(Message.MessageType.valueOf(request.getMessageType()));

        Message savedMessage = messageRepository.save(message);

        // Chat'in son mesaj zamanını güncelle
        chat.updateLastMessageTime();
        chatRepository.save(chat);

        // Response oluştur
        MessageResponse response = mapToMessageResponse(savedMessage, senderId);

        // WebSocket ile mesajı gönder
        sendRealTimeMessage(response, chat);

        log.info("Message sent successfully: {}", savedMessage.getId());
        return response;
    }

    public List<MessageResponse> getChatMessages(Long chatId, Long userId, Pageable pageable) {
        log.info("Getting messages for chat: {} by user: {}", chatId, userId);

        // Erişim kontrolü
        if (!chatRepository.findByIdAndUserId(chatId, userId).isPresent()) {
            throw new RuntimeException("Chat bulunamadı veya erişim yetkiniz yok");
        }

        Page<Message> messagesPage = messageRepository.findByChatIdOrderBySentAtDesc(chatId, pageable);
        List<Message> messages = messagesPage.getContent();
        
        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messages) {
            MessageResponse response = mapToMessageResponse(message, userId);
            messageResponses.add(response);
        }

        return messageResponses;
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        log.info("Marking messages as read for chat: {} by user: {}", chatId, userId);

        // Erişim kontrolü
        if (!chatRepository.findByIdAndUserId(chatId, userId).isPresent()) {
            throw new RuntimeException("Chat bulunamadı veya erişim yetkiniz yok");
        }

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByChatIdAndUserId(chatId, userId);
        
        for (Message message : unreadMessages) {
            message.markAsRead();
        }
        
        if (!unreadMessages.isEmpty()) {
            messageRepository.saveAll(unreadMessages);
            log.info("Marked {} messages as read", unreadMessages.size());
        }
    }

    public long getUnreadCount(Long chatId, Long userId) {
        return messageRepository.countUnreadMessagesByChatIdAndUserId(chatId, userId);
    }

    private MessageResponse mapToMessageResponse(Message message, Long currentUserId) {
        MessageResponse response = new MessageResponse();

        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setChatId(message.getChat().getId());

        // Gönderen bilgileri
        User sender = message.getSender();
        response.setSenderId(sender.getId());
        response.setSenderUsername(sender.getUsername());
        response.setSenderName(sender.getName() + " " + sender.getSurname());

        // Mesaj bilgileri
        response.setSentAt(message.getSentAt());
        response.setRead(message.isRead());
        response.setReadAt(message.getReadAt());
        response.setMessageType(message.getMessageType().toString());

        // Kendi mesajı mı kontrolü
        response.setOwnMessage(sender.getId().equals(currentUserId));

        return response;
    }

    private void sendRealTimeMessage(MessageResponse messageResponse, Chat chat) {
        try {
            // Employer'a mesaj gönder
            String employerDestination = "/user/" + chat.getEmployer().getUsername() + "/queue/messages";
            messagingTemplate.convertAndSend(employerDestination, messageResponse);

            // Freelancer'a mesaj gönder  
            String freelancerDestination = "/user/" + chat.getFreelancer().getUsername() + "/queue/messages";
            messagingTemplate.convertAndSend(freelancerDestination, messageResponse);

            log.info("Real-time message sent to chat participants");
        } catch (Exception e) {
            log.error("Failed to send real-time message: {}", e.getMessage());
        }
    }
}