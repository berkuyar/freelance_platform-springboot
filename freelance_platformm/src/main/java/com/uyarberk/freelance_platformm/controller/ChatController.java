package com.uyarberk.freelance_platformm.controller;

import com.uyarberk.freelance_platformm.dto.ChatResponse;
import com.uyarberk.freelance_platformm.dto.MessageRequest;
import com.uyarberk.freelance_platformm.dto.MessageResponse;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.ChatService;
import com.uyarberk.freelance_platformm.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    // REST Endpoint: Kullanıcının chat listesini getir
    @GetMapping("/my-chats")
    public ResponseEntity<List<ChatResponse>> getMyChats(@AuthenticationPrincipal User user) {
        List<ChatResponse> chats = chatService.getUserChats(user.getId());
        return ResponseEntity.ok(chats);
    }

    // REST Endpoint: Belirli bir chat'i getir
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        Optional<ChatResponse> chat = chatService.getChatById(chatId, user.getId());
        
        if (chat.isPresent()) {
            return ResponseEntity.ok(chat.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // REST Endpoint: Chat mesajlarını getir
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponse>> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<MessageResponse> messages = messageService.getChatMessages(chatId, user.getId(), pageable);
        return ResponseEntity.ok(messages);
    }

    // REST Endpoint: Mesajları okundu olarak işaretle
    @PutMapping("/{chatId}/mark-read")
    public ResponseEntity<String> markMessagesAsRead(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        messageService.markMessagesAsRead(chatId, user.getId());
        return ResponseEntity.ok("Mesajlar okundu olarak işaretlendi");
    }

    // REST Endpoint: Chat'i devre dışı bırak
    @DeleteMapping("/{chatId}")
    public ResponseEntity<String> deactivateChat(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        chatService.deactivateChat(chatId, user.getId());
        return ResponseEntity.ok("Chat devre dışı bırakıldı");
    }

    // WebSocket Endpoint: Mesaj gönder (Real-time)
    @MessageMapping("/send-message")
    public void sendMessage(@Payload @Valid MessageRequest messageRequest, Principal principal) {
        try {
            log.info("Received WebSocket message from: {}", principal.getName());
            
            // Principal'dan User objesi al (JWT Interceptor sayesinde)
            if (principal instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
                User user = (User) auth.getPrincipal();
                
                // Mesajı gönder
                messageService.sendMessage(messageRequest, user.getId());
                
                log.info("Message sent via WebSocket from user: {}", user.getUsername());
            } else {
                throw new RuntimeException("Geçersiz authentication");
            }
        } catch (Exception e) {
            log.error("Error sending WebSocket message: {}", e.getMessage());
        }
    }

    // REST Endpoint: HTTP ile mesaj gönder (alternatif)
    @PostMapping("/send-message")
    public ResponseEntity<MessageResponse> sendMessageHttp(
            @Valid @RequestBody MessageRequest messageRequest, 
            @AuthenticationPrincipal User user) {
        
        MessageResponse response = messageService.sendMessage(messageRequest, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // WebSocket Endpoint: Chat'e katıl (typing indicator vs. için)
    @MessageMapping("/join-chat")
    public void joinChat(@Payload String chatId, Principal principal) {
        log.info("User {} joined chat: {}", principal.getName(), chatId);
        // Chat'e katılma logicini burada implement edebiliriz
    }

    // WebSocket Endpoint: Chat'ten ayrıl
    @MessageMapping("/leave-chat")
    public void leaveChat(@Payload String chatId, Principal principal) {
        log.info("User {} left chat: {}", principal.getName(), chatId);
        // Chat'ten ayrılma logicini burada implement edebiliriz
    }

    // REST Endpoint: Okunmamış mesaj sayısını getir
    @GetMapping("/{chatId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        // Önce erişim kontrolü
        if (!chatService.hasAccessToChat(chatId, user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        long unreadCount = messageService.getUnreadCount(chatId, user.getId());
        return ResponseEntity.ok(unreadCount);
    }
}