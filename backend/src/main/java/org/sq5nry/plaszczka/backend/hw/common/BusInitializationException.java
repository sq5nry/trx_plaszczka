package org.sq5nry.plaszczka.backend.hw.common;

public class BusInitializationException extends Exception {
    public BusInitializationException(String msg) {
        super(msg);
    }

    public BusInitializationException(String msg, Exception e) {
        super(msg, e);
    }
}
