package com.uyarberk.freelance_platformm.config;

import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtWebSocketInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                // Authorization header'dan token al
                String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    
                    // Token doğrula
                    if (jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token)) {
                        Long userId = jwtUtil.extractUserId(token);
                        String username = jwtUtil.extractUsername(token);
                        String role = jwtUtil.extractRole(token);
                        
                        // User'ı database'den getir (isteğe bağlı - ekstra güvenlik için)
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. ID: " + userId));
                        
                        // Authentication oluştur
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        );
                        
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(user, null, authorities);
                        
                        // WebSocket session'ına authentication set et
                        accessor.setUser(authentication);
                        
                        log.info("WebSocket authenticated user: {} (ID: {})", username, userId);
                    } else {
                        log.warn("Invalid or expired WebSocket token");
                        throw new RuntimeException("Geçersiz token");
                    }
                } else {
                    log.warn("No Authorization header in WebSocket connection");
                    throw new RuntimeException("Authorization header gereklidir");
                }
                
            } catch (Exception e) {
                log.error("WebSocket authentication failed: {}", e.getMessage());
                throw new RuntimeException("WebSocket authentication başarısız: " + e.getMessage());
            }
        }
        
        return message;
    }
}