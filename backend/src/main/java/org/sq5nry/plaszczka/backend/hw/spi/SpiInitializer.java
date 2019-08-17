package org.sq5nry.plaszczka.backend.hw.spi;

import com.pi4j.wiringpi.Spi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.hw.common.BusInitializationException;

@Component
public class SpiInitializer {
    private static final Logger logger = LoggerFactory.getLogger(SpiInitializer.class);

    @Autowired
    private SpiConfiguration spiConfig;

    public void initialize() throws BusInitializationException {
        logger.info("initializing SPI, channel={}, speed={}", spiConfig.getSpiChannel(), spiConfig.getSpiSpeed());
        if (spiConfig.isSpiSimulated()) {
            logger.info("initializing dummy SPI, no operation");
        } else {
            int fdSpi = Spi.wiringPiSPISetup(spiConfig.getSpiChannel(), spiConfig.getSpiSpeed());
            if (fdSpi <= -1) {
                logger.error("SPI bus setup failed for channel {}, FD={}", spiConfig.getSpiChannel(), fdSpi);
                throw new BusInitializationException("SPI bus setup failed for channel " + spiConfig.getSpiChannel() + ", fd=" + fdSpi);
            } else {
                logger.debug("SPI initialized for channel {}, FD={}", spiConfig.getSpiChannel(), fdSpi);
            }
        }
    }
}
