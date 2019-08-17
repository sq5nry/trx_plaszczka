package org.sq5nry.plaszczka.frontend.comm;

import org.apache.log4j.Logger;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class WsStompClient {
    private static final Logger logger = Logger.getLogger(WsStompClient.class);

    private String url;

    public WsStompClient(String backendStompUrl) {
        this.url = backendStompUrl;
    }

    public VAgcStreamController initialize() {
        logger.info("initializing");
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new StringMessageConverter());  //TODO oth for json

        StompSessionHandler sessionHandler = new VAgcStompSessionHandler(stompClient, url);
        return (VAgcStreamController) sessionHandler;
    }
}
