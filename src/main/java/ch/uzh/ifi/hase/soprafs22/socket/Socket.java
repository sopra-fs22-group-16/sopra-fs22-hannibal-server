package ch.uzh.ifi.hase.soprafs22.socket;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Socket
 * This class is responsible for creating the socket configuration to communicate
 * with the client.
 */

@Configuration
@EnableWebSocketMessageBroker
public class Socket implements WebSocketMessageBrokerConfigurer{

    private static final String WEBSOCKET_PREFIX = "/topic";
    private static final String WEBSOCKET_SUFFIX = "/hannibal-websocket";
    private static final String ORIGIN_LOCALHOST = "http://localhost:3000/**";
    private static final String ORIGIN_PROD = "https://sopra-fs22-group-16-client.herokuapp.com/**";

    @Override
    public void configureMessageBroker(@NotNull MessageBrokerRegistry config) {
        config.enableSimpleBroker(WEBSOCKET_PREFIX);
    }

    @Override
    public void registerStompEndpoints(@NotNull StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_SUFFIX)
                .setAllowedOriginPatterns(ORIGIN_LOCALHOST,ORIGIN_PROD)
                .withSockJS();
    }
}
