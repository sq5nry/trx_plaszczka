package org.sq5nry.plaszczka.frontend.comm;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.MessageHandler;
import java.lang.reflect.Type;

class VAgcStompSessionHandler implements StompSessionHandler, VAgcStreamController {
    private static final Logger logger = Logger.getLogger(VAgcStompSessionHandler.class);

    public static final String APP_VAGC_STREAM_CONTROL = "/app/vagc_stream_control";
    public static final String TOPIC_VAGC = "/topic/vagc";
    public static final String APP_CMD_START = "start";
    public static final String APP_CMD_STOP = "stop";

    private final WebSocketStompClient stompClient;
    private final String url;
    private StompSession session;
    private MessageHandler.Whole handler;

    public VAgcStompSessionHandler(WebSocketStompClient stompClient, String url) {
        this.stompClient = stompClient;
        this.url = url;
    }

    @Override
    public void connect() {
        logger.info("connecting, url=" + url);
        stompClient.connect(url, this);
    }

    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        logger.info("afterConnected: " + session + ", " + stompHeaders);

        session.subscribe(TOPIC_VAGC, this);
        this.session = session;

        logger.info("setting period");
        session.send(APP_VAGC_STREAM_CONTROL, ""+period);
    }

    @Override
    public void setPeriod(int period) {
        this.period = period;
    }

    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        logger.warn("handleException: " + stompSession + ", " + stompCommand + ", " + stompHeaders + ", data=" + new String(bytes));
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
         if (logger.isDebugEnabled()) {
             logger.debug("handleFrame: rx=" + payload);
         }
         if (handler != null) {
             handler.onMessage(payload);    //TODO prevent string msg from bckend
         } else {
             logger.warn("no frame handler, payload=" + payload);
         }
    }

    // API

    private int period;

    @Override
    public void addMessageHandler(MessageHandler.Whole handler) {
        this.handler = handler;
        logger.info("addMessageHandler: " + handler);
    }

    @Override
    public void start() {
        logger.info("requesting for VAgc stream");
        session.send(APP_VAGC_STREAM_CONTROL, APP_CMD_START);
    }

    @Override
    public void stop() {
        if (session.isConnected()) {
            logger.info("stop requested");
            session.send(APP_VAGC_STREAM_CONTROL, APP_CMD_STOP);
            session.disconnect();
        } else {
            logger.info("stop requested but session is disconnected");
        }
    }
}
