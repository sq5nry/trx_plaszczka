package org.sq5nry.plaszczka.frontend.comm;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import javax.websocket.MessageHandler;
import java.lang.reflect.Type;

public class VAgcStompSessionHandler implements StompSessionHandler, VAgcStreamController {
    private static final Logger logger = Logger.getLogger(VAgcStompSessionHandler.class);

    private StompSession session;
    private MessageHandler.Whole handler;

    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        logger.debug("afterConnected: " + session + ", " + stompHeaders);

        session.subscribe("/topic/vagc", this);
        this.session = session;
    }

    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        logger.debug("handleException: " + stompSession + ", " + stompCommand + ", " + stompHeaders, throwable);
    }

    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        logger.warn("handleTransportError: " + stompSession, throwable);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
         logger.info("handleFrame: rx=" + payload);
         if (handler != null) {
             handler.onMessage(payload);
         } else {
             logger.warn("no frame handler, payload=" + payload);
         }
    }

    // API

    private int period;

    @Override
    public void addMessageHandler(MessageHandler.Whole handler, int period) {
        this.handler = handler;
        this.period = period;
        logger.info("addMessageHandler: " + handler + ", requested period=" + period);
    }

    @Override
    public void start() {
        logger.info("period set");
        session.send("/app/vagc_stream_control", ""+period);
        logger.info("start requested");
        session.send("/app/vagc_stream_control", "start");
    }

    @Override
    public void stop() {
        logger.info("stop requested");
        session.send("/app/vagc_stream_control", "stop");
        session.disconnect();
    }
}
