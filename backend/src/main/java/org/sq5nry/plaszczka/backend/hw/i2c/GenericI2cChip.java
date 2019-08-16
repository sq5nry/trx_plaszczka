package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;

import java.io.IOException;

public class GenericI2cChip extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericI2cChip.class);

    private I2CBus i2CBus;
    private I2CDevice device;

    public GenericI2cChip(int address) {
        super(address);
    }

    @Override
    public boolean needsGpio() {
        return false;
    }

    public GenericChip initialize() throws ChipInitializationException {
        logger.debug("initialize: entering");
        try {
            device = i2CBus.getDevice(address);
            if ((address >= 0x30 && address <= 0x37) || (address >= 0x50 && address <= 0x5F)) {
                device.read();
            } else {
                device.write((byte) 0);
            }
            initialized = true;
        } catch (IOException e) {
            logger.warn("initialization failed", e);
            initialized = false;
            throw new ChipInitializationException(e);
        }
        return this;
    }


    public I2CDevice getDevice() {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return device;
    }

    public void setI2CBus(I2CBus i2CBus) {
        this.i2CBus = i2CBus;
    }
}
