package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Wrapper for SPI as Spring thing. Could this be done springlier?
 */
@Component
public class SPIDeviceProvider {
    private static final Logger logger = LoggerFactory.getLogger(SPIDeviceProvider.class);

    @Value("${spi.channel.number}")
    private Integer channelNr;

    public SpiDevice getSpiDevice() throws IOException {
        logger.debug("spi hw {}", channelNr);
        return SpiFactory.getInstance(SpiChannel.getByNumber(channelNr));
    }
}
