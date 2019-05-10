package com.example.partybauxserver.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * The WebSocketConfig class is what enables our project to use
 * websockets in order to communicate between devices in real time.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    	
		/*
		 * config.enableSimpleBroker("/topic", "/queue", "/exchange");
		 * config.setApplicationDestinationPrefixes("/topic", "/queue");
		 * config.setUserDestinationPrefix("/user");
		 */
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //registry.addEndpoint("/gs-guide-websocket").withSockJS();
    	registry.addEndpoint("/example-endpoint").withSockJS();
    }

}