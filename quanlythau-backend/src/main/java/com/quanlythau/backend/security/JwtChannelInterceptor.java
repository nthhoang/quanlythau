package com.quanlythau.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Xac thuc JWT cho WebSocket. Trinh duyet KHONG cho gan header tuy y luc mo
 * ket noi WebSocket, nen khong the xac thuc o buoc bat tay HTTP nhu REST binh
 * thuong (buoc do da duoc permitAll trong SecurityConfig). Thay vao do, token
 * duoc gui nhu 1 STOMP header trong frame CONNECT (buoc dau tien SAU KHI da
 * ket noi WebSocket) - interceptor nay chan dung frame CONNECT do de doc va
 * kiem tra token, tu choi thang neu thieu/sai/het han.
 */
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessagingException("Thiếu token xác thực");
            }

            String token = authHeader.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new MessagingException("Token hết hạn hoặc không hợp lệ");
                }
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authToken);
            } catch (MessagingException e) {
                throw e;
            } catch (Exception e) {
                throw new MessagingException("Xác thực thất bại: " + e.getMessage());
            }
        }

        return message;
    }
}
