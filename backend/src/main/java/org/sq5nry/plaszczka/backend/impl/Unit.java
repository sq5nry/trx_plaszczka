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
public abstract class Unit {
    private static final Logger logger = LoggerFactory.getLogger(Unit.class);

    private I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();
    State state = State.CREATED;

    public enum State { CREATED, CHIPSET_INITIALIZED, UNIT_INITIALIZED, FAILED }

    public Unit(I2CBusProvider i2cBusProv) throws IOException, I2CFactory.UnsupportedBusNumberException {
        bus = i2cBusProv.getBus();
    }

    public void addToChipset(GenericChip chip) {
        chipset.put(chip.getAddress(), chip);
    }

    public GenericChip getChip(int address) {
        return chipset.get(address);
    }

    public void initializeChipset() {
        state = State.CREATED;
        try {
            for(GenericChip chip: chipset.values()) {
                chip.setI2CBus(bus);
                chip.initialize();
            }
            state = State.CHIPSET_INITIALIZED;
        } catch(IOException e) {
            logger.warn("unit chipset initialization failed", e);
            state = State.FAILED;
        }
        logger.debug("chipset created & initialized");
    }

    public void initializeUnit() {
        logger.debug("default unit initialization: no-op");
    }

    public State getState() {
        return state;
    }
}
