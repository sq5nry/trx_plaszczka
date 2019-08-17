package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Wrapper for I2CBus as Spring thing. Could this be done springlier?
 */
@Component
public class I2CBusProvider {
    private static final Logger logger = LoggerFactory.getLogger(I2CBusProvider.class);

    @Value("${i2c.bus.number}")
    private int busNr;

    public I2CBus getBus() throws IOException, I2CFactory.UnsupportedBusNumberException {
        logger.debug("getBus: {}", busNr);
        return I2CFactory.getInstance(busNr);
    }
}
