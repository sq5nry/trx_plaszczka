package org.sq5nry.plaszczka.backend.hw.spi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpiConfiguration {
    @Value("${spi.simulated}")
    private boolean isSpiSimulated;

    public boolean isSpiSimulated() {
        return isSpiSimulated;
    }
}
