package com.example.doanjava.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatHandshakeHandler chatHandshakeHandler;

    public WebSocketConfig(ChatHandshakeHandler chatHandshakeHandler) {
        this.chatHandshakeHandler = chatHandshakeHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/queue" là kênh riêng từng người (server đẩy tới /user/queue/...).
        // Trước đây chỉ có "/topic" nên tin gửi theo từng người sẽ không tới được.
        registry.enableSimpleBroker("/queue");

        // Tin trình duyệt gửi lên server phải bắt đầu bằng /app (ví dụ /app/chat.gui)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // - Bỏ setAllowedOriginPatterns("*"): cho mọi trang web khác kết nối bằng phiên đăng nhập của người dùng
        //   là lỗ hổng bảo mật. Mặc định Spring chỉ cho cùng tên miền.
        // - Bỏ withSockJS(): mọi trình duyệt hiện nay đều hỗ trợ WebSocket, bỏ đi thì khỏi cần thư viện
        //   SockJS và khỏi vướng CSRF ở các đường truyền dự phòng.
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(chatHandshakeHandler);   // đặt tên kết nối = id tài khoản
    }
}