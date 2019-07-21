package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sq5nry.plaszczka.backend.impl.Unit.State.FAILED;

/**
 * A physical realization of a functional module, usually enclosed in a metal box.
 */
public abstract class Unit {
    private static final Logger logger = LoggerFactory.getLogger(Unit.class);

    private I2CBus bus;
    private Map<Integer, GenericChip> chipset;
    State state = State.CREATED;

    public enum State { CREATED, CHIPSET_INITIALIZED, UNIT_INITIALIZED, FAILED }

    public Unit(I2CBusProvider i2cBusProv) throws Exception {
        bus = i2cBusProv.getBus();

        createChipset();
        initializeChipset();
        if (state == State.CHIPSET_INITIALIZED) {
            try {
                initializeUnit();
                state = State.UNIT_INITIALIZED;
            } catch(Exception e) {
                logger.debug("unit initialization failed", e);
                state = FAILED;
            }
        }
    }

    private void createChipset() {
        List<GenericChip> chipList = new ArrayList<>();
        createChipset(chipList);
        logger.debug("got {} chips", chipList.size());
        chipset = new HashMap<>();
        for(GenericChip chip: chipList) {
            logger.debug("adding to chipset {}", chip);
            chipset.put(chip.getAddress(), chip);
        }
    }

    public abstract void createChipset(List<GenericChip> chipset);

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

    public void initializeUnit() throws Exception {
        logger.debug("default unit initialization: no-op");
        state = State.UNIT_INITIALIZED;
    }

    public State getState() {
        return state;
    }
}
