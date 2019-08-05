package org.sq5nry.plaszczka.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.sq5nry.plaszczka.backend.controller.IfAmpController;

@Component
public class StompEventListener implements ApplicationListener<SessionConnectEvent> {
    private static final Logger logger = LoggerFactory.getLogger(StompEventListener.class);

    @Autowired
    private IfAmpController ifAmpCtr;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("onApplicationEvent: sessionId={}, command={}", sha.getSessionId(), sha.getCommand());
    }

    @EventListener
    public void onSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("onSocketConnected: {}", sha.getDetailedLogMessage(event.getMessage()));
    }
    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        logger.info("onSocketDisconnected: {}", sha.getDetailedLogMessage(event.getMessage()));
        ifAmpCtr.stop();
    }
}