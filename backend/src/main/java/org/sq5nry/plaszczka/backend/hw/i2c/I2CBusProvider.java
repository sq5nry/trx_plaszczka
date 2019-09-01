package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.hw.common.BusInitializationException;

import java.io.IOException;

/**
 * Wrapper for I2CBus as Spring thing. Could this be done springlier?
 */
@Component
public class I2CBusProvider {
    private static final Logger logger = LoggerFactory.getLogger(I2CBusProvider.class);

    @Autowired
    private I2CConfiguration i2cConfig;


    public I2CBus getBus() throws IOException, I2CFactory.UnsupportedBusNumberException {
        logger.debug("getBus: {}", i2cConfig.getBusNr());
        return I2CFactory.getInstance(i2cConfig.getBusNr());
    }

    public void initialize() throws BusInitializationException {
        logger.info("initializing I2C");
        logger.debug("setting i2c factory: {}", i2cConfig.getI2cProviderClass());
        try {
            Class clazz = Class.forName(i2cConfig.getI2cProviderClass());
            I2CProviderImpl provider = (I2CProviderImpl) clazz.newInstance();
            I2CFactory.setFactory(provider);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new BusInitializationException("I2C initialization failed", e);
        }
    }
}
