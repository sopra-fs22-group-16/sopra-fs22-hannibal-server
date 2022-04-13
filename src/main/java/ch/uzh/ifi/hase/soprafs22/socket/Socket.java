package ch.uzh.ifi.hase.soprafs22.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/hannibal-websocket")
                .setAllowedOrigins("http://localhost:3000","https://sopra-fs22-group-16-client.herokuapp.com")
                .withSockJS();
    }
}
