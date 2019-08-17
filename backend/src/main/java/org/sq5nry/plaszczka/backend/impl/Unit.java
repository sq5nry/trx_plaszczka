package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2cChip;
import org.sq5nry.plaszczka.backend.hw.gpio.GpioControllerProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.spi.SpiConfiguration;

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

    private SpiConfiguration spiConfig;
    private GpioController gpioController;
    private Map<Integer, GenericChip> chipset;
    State state = State.CREATED;

    public enum State { CREATED, CHIPSET_INITIALIZED, UNIT_INITIALIZED, FAILED }

    public Unit(I2CBusProvider i2cBusProv) throws Exception {
        this(i2cBusProv, null, null);
    }

    public Unit(I2CBusProvider i2cBusProv, SpiConfiguration spiConfig, GpioControllerProvider gpioProv) throws Exception {
        logger.info("=============== creating {}", getName());
        this.spiConfig = spiConfig;
        bus = i2cBusProv.getBus();
        if (gpioProv != null) gpioController = gpioProv.getGpioController();

        createChipset();
        initializeChipset();
        if (state == State.CHIPSET_INITIALIZED) {
            try {
                initializeUnit();
                state = State.UNIT_INITIALIZED;
            } catch(Exception e) {
                logger.warn("unit initialization failed", e);
                state = FAILED;
            }
        }
        logger.info("=============== created {}", getName());
    }

    private void createChipset() throws Exception {
        logger.info("createChipset: entering");
        List<GenericChip> chipList = new ArrayList<>();
        createChipset(chipList);
        logger.info("got {} chips", chipList.size());
        chipset = new HashMap<>();
        for(GenericChip chip: chipList) {
            logger.info("adding to chipset {}", chip);
            chipset.put(chip.getAddress(), chip);
        }
    }

    public abstract void createChipset(List<GenericChip> chipset) throws Exception;

    public GenericChip getChip(int address) {
        return chipset.get(address);
    }

    /**
     * Get SPI chip (no address)
     * TODO: assumed one chip on SPI bus
     * @return
     */
    public GenericChip getChip() {
        return chipset.values().iterator().next();  //TODO could be 2 chips on SPI
    }

    public void initializeChipset() {
        logger.info("initializeChipset: entering");
        state = State.CREATED;
        try {
            for(GenericChip chip: chipset.values()) {
                if (chip instanceof GenericI2cChip) {
                    logger.info("initializeChipset: setting I2C for chip={}", chip);
                    ((GenericI2cChip) chip).setI2CBus(bus);
                } else {
                    logger.info("initializeChipset: not setting specific SPI bus, using the general one {}", chip);
                }

                if (chip.needsGpio()) {
                    logger.info("initializeChipset: adding GPIO controller for a chip");
                    chip.setGpioController(gpioController);
                }

                chip.initialize();
                logger.info("initializeChipset: initialization of chip={} complete", chip);
            }
            state = State.CHIPSET_INITIALIZED;
        } catch(ChipInitializationException e) {
            logger.warn("unit chipset initialization failed", e);
            state = State.FAILED;
        }
        logger.info("chipset created & initialized");
    }

    public void initializeUnit() throws Exception {
        logger.debug("default unit initialization: no-op");
        state = State.UNIT_INITIALIZED;
    }

    protected SpiConfiguration getSpiConfig() {
        return spiConfig;
    }

    public State getState() {
        return state;
    }

    public abstract String getName();
}
