package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.client.RequestSender;

public class BaseCommunicator {
    protected final RequestSender requestSender;

    public BaseCommunicator(RequestSender requestSender) {
        this.requestSender = requestSender;
    }
}
