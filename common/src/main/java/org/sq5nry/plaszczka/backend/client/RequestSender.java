package org.sq5nry.plaszczka.backend.client;

import java.io.IOException;

public interface RequestSender {
    String sendRequest(String path) throws IOException;
}