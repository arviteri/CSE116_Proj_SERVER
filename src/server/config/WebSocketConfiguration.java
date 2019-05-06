package server.config;


import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure subscription and message endpoints.
     *
     *  Active Subscription Endpoints: [
     *       /game/players,
     *       /game/projectiles,
     *       /game/zombies,
     *       /user/queue/join
     *       /user/queue/all_players
     *  ]
     *
     *  Active Server Message Endpoints: [
     *      /app/join,
     *      /app/leave,
     *      /app/move,
     *      /app/shoot
     *  ]
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/game", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Configure connection endpoints.
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect_desktop").setAllowedOrigins("*");
        registry.addEndpoint("/connect_web").setAllowedOrigins("*").withSockJS();
    }
}
