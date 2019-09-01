package org.sq5nry.plaszczka.backend.hw.spi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SPIConfiguration {
    @Value("${spi.simulated}")
    private boolean isSpiSimulated;

    @Value("${spi.channel.number}")
    private int spiChannel;

    @Value("${spi.speed}")
    private int spiSpeed;

    public int getSpiChannel() {
        return spiChannel;
    }

    public int getSpiSpeed() {
        return spiSpeed;
    }

    public boolean isSpiSimulated() {
        return isSpiSimulated;
    }
}
