package com.as1270.optimiser.config;

import com.as1270.optimiser.controllers.GeneticAlgorithmHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GeneticAlgorithmHandler(), "/optimise-socket") //this guy handles all traffic (*) to optimise-connect
                .setAllowedOrigins("*");
    }
}