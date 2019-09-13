package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.ConsoleColours;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;

import java.io.IOException;

public class GenericI2CChip extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericI2CChip.class);

    private I2CBus i2CBus;
    private I2CDevice device;

    public GenericI2CChip(int address) {
        super(address);
    }

    @Override
    public boolean needsGpio() {
        return false;
    }

    public GenericChip initialize() throws ChipInitializationException {
        logger.info("generic I2C device initializer: entering");
        try {
            device = i2CBus.getDevice(address);
            if ((address >= 0x30 && address <= 0x37) || (address >= 0x50 && address <= 0x5F)) {
                device.read();
            } else if (address == 0x29) {
                //specific for AD7999, it won't accept a byte write
                device.read(new byte[2], 0, 2);
            } else {
                device.write((byte) 0);
            }
            initialized = true;
            logger.info("generic I2C device initializer: done");
        } catch (IOException e) {
            logger.warn(ConsoleColours.RED + "initialization failed for " + getClass().getSimpleName() + ConsoleColours.RESET, e);
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
