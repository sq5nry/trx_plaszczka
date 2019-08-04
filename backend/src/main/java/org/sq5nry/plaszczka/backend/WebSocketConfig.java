package org.sq5nry.plaszczka.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(new OwnErrorHandler());
        registry.addEndpoint("/vagc-websocket");//.withSockJS();
    }

    class OwnErrorHandler extends StompSubProtocolErrorHandler {
        @Override
        public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
            logger.info("handleClientMessageProcessingError: {} {}", clientMessage, ex);
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }

        @Override
        public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
            logger.info("handleErrorMessageToClient: {} {}", errorMessage);
            return super.handleErrorMessageToClient(errorMessage);
        }

        @Override
        protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
            logger.info("handleInternal: {} {}", errorHeaderAccessor, cause);
            return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
        }
    }
}