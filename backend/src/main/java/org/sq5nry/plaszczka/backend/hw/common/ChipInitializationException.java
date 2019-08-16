package org.sq5nry.plaszczka.backend.hw.common;

import java.io.IOException;

public class ChipInitializationException extends Exception {
    public ChipInitializationException(String msg) {
        super(msg);
    }

    public ChipInitializationException(IOException e) {
        super(e);
    }
}
