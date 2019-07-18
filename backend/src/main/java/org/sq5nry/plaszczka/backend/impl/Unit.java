package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A physical realization of a functional module, usually enclosed in a metal box.
 */
public class Unit {
    private static final Logger logger = LoggerFactory.getLogger(Unit.class);

    private I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();
    private State state = State.CREATED;

    public enum State { CREATED, INITIALIZED, FAILED }

    public Unit(I2CBusProvider i2cBusProv) throws IOException, I2CFactory.UnsupportedBusNumberException {
        bus = i2cBusProv.getBus();
    }

    public void addToChipset(GenericChip chip) {
        chipset.put(chip.getAddress(), chip);
    }

    public GenericChip getChip(int address) {
        return chipset.get(address);
    }

    public void initializeChipset() throws IOException {
        state = State.CREATED;
        try {
            for(GenericChip chip: chipset.values()) {
                chip.setI2CBus(bus);
                chip.initialize();
            }
            state = State.INITIALIZED;
        } catch(IOException e) {
            state = State.FAILED;
            throw e;
        }
        logger.debug("chipset created & initialized");
    }

    public State getState() {
        return state;
    }
}
