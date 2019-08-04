package org.sq5nry.plaszczka.frontend.comm;

import javax.websocket.MessageHandler;

public interface VAgcStreamController {
    void addMessageHandler(MessageHandler.Whole handler);
    void connect();
    void setPeriod(int period);
    void start();
    void stop();
}
