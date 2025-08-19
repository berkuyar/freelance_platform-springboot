package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.ChatResponse;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.*;
import com.uyarberk.freelance_platformm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatResponse createChatFromBid(Long bidId) {
        log.info("Creating chat for bid: {}", bidId);
        
        // Bid'i getir
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Teklif bulunamadı. ID: " + bidId));

        // Zaten chat var mı kontrol et
        Optional<Chat> existingChat = chatRepository.findByBidId(bidId);
        if (existingChat.isPresent()) {
            log.warn("Chat already exists for bid: {}", bidId);
            return mapToChatResponse(existingChat.get(), bid.getUser().getId());
        }

        // Yeni chat oluştur
        Chat chat = new Chat();
        chat.setBid(bid);
        chat.setEmployer(bid.getPost().getUser()); // Post sahibi = Employer
        chat.setFreelancer(bid.getUser()); // Bid veren = Freelancer

        Chat savedChat = chatRepository.save(chat);
        log.info("Chat created successfully: {}", savedChat.getId());

        return mapToChatResponse(savedChat, bid.getUser().getId());
    }

    public List<ChatResponse> getUserChats(Long userId) {
        log.info("Getting chats for user: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Kullanıcı bulunamadı. ID: " + userId);
        }

        List<Chat> chats = chatRepository.findUserChats(userId);
        List<ChatResponse> chatResponses = new ArrayList<>();
        
        for (Chat chat : chats) {
            ChatResponse response = mapToChatResponse(chat, userId);
            chatResponses.add(response);
        }
        
        return chatResponses;
    }

    public Optional<ChatResponse> getChatById(Long chatId, Long userId) {
        log.info("Getting chat {} for user {}", chatId, userId);
        
        Optional<Chat> chatOpt = chatRepository.findByIdAndUserId(chatId, userId);
        
        if (chatOpt.isPresent()) {
            return Optional.of(mapToChatResponse(chatOpt.get(), userId));
        }
        
        return Optional.empty();
    }

    public boolean hasAccessToChat(Long chatId, Long userId) {
        return chatRepository.findByIdAndUserId(chatId, userId).isPresent();
    }

    @Transactional
    public void deactivateChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdAndUserId(chatId, userId)
                .orElseThrow(() -> new RuntimeException("Chat bulunamadı veya erişim yetkiniz yok"));

        chat.setActive(false);
        chatRepository.save(chat);
        
        log.info("Chat {} deactivated by user {}", chatId, userId);
    }

    private ChatResponse mapToChatResponse(Chat chat, Long currentUserId) {
        ChatResponse response = new ChatResponse();
        
        response.setId(chat.getId());
        response.setBidId(chat.getBid().getId());
        response.setPostTitle(chat.getBid().getPost().getTitle());
        
        // Employer bilgileri
        User employer = chat.getEmployer();
        response.setEmployerId(employer.getId());
        response.setEmployerUsername(employer.getUsername());
        response.setEmployerName(employer.getName() + " " + employer.getSurname());
        
        // Freelancer bilgileri
        User freelancer = chat.getFreelancer();
        response.setFreelancerId(freelancer.getId());
        response.setFreelancerUsername(freelancer.getUsername());
        response.setFreelancerName(freelancer.getName() + " " + freelancer.getSurname());
        
        // Chat bilgileri
        response.setCreatedAt(chat.getCreatedAt());
        response.setLastMessageAt(chat.getLastMessageAt());
        response.setActive(chat.isActive());
        
        // Son mesaj bilgisi
        Message lastMessage = messageRepository.findLastMessageByChatId(chat.getId());
        if (lastMessage != null) {
            response.setLastMessageContent(lastMessage.getContent());
            response.setLastMessageTime(lastMessage.getSentAt());
            response.setLastMessageSender(lastMessage.getSender().getUsername());
        }
        
        // Okunmamış mesaj sayısı
        long unreadCount = messageRepository.countUnreadMessagesByChatIdAndUserId(chat.getId(), currentUserId);
        response.setUnreadCount(unreadCount);
        
        return response;
    }
}