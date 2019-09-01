package org.sq5nry.plaszczka.backend.hw.common;

import java.io.IOException;

public class ChipInitializationException extends Exception {
    public ChipInitializationException(Exception e) {
        super(e);
    }

    public ChipInitializationException(String msg) {
        super(msg);
    }

    public ChipInitializationException(String msg, IOException e) {
        super(msg, e);
    }
}
