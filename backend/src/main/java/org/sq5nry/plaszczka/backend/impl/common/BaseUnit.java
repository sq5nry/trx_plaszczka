package org.sq5nry.plaszczka.backend.impl.common;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.common.Unit;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.ConsoleColours;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.gpio.GpioControllerProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2CChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.spi.SPIConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sq5nry.plaszczka.backend.common.Unit.State.FAILED;

/**
 * A physical realization of a functional module, usually enclosed in a metal box.
 */
public abstract class BaseUnit implements Unit {
    private static final Logger logger = LoggerFactory.getLogger(BaseUnit.class);

    private I2CBus bus;

    private SPIConfiguration spiConfig;
    private GpioController gpioController;
    private Map<Integer, GenericChip> chipset;
    private State state = State.CREATED;

    public BaseUnit(I2CBusProvider i2cBusProv) throws Exception {
        this(i2cBusProv, null, null);
    }

    public BaseUnit(I2CBusProvider i2cBusProv, SPIConfiguration spiConfig, GpioControllerProvider gpioProv) throws Exception {
        logger.info("=============== creating {}", getName());
        this.spiConfig = spiConfig;
        bus = i2cBusProv.getBus();
        if (gpioProv != null) gpioController = gpioProv.getGpioController();

        createChipset0();
        initializeChipsetAndUnit();
        logger.info("=============== created {}", getName());
    }

    @Override
    public void initializeChipsetAndUnit() {
        initializeChipset();
        if (state == State.CHIPSET_INITIALIZED) {
            try {
                initializeUnit();
                state = State.UNIT_INITIALIZED;
                logger.info("{}{} initialized{}", ConsoleColours.GREEN_BOLD, getName(), ConsoleColours.RESET);
            } catch(Exception e) {
                logger.warn("unit initialization failed", e);
                state = FAILED;
            }
        }
    }

    private void createChipset0() {
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

    public GenericChip getChip(int address) {
        return chipset.get(address);
    }

    public void initializeChipset() {
        logger.info("initializeChipset: entering");
        state = State.CREATED;
        try {
            for(GenericChip chip: chipset.values()) {
                if (chip instanceof GenericI2CChip) {
                    logger.info("initializeChipset: setting I2C for chip={}", chip);
                    ((GenericI2CChip) chip).setI2CBus(bus);
                } else {
                    logger.info("initializeChipset: not setting specific SPI bus, using the general one {}", chip);
                }

                if (chip.needsGpio()) {
                    logger.info("initializeChipset: adding GPIO controller for a chip");
                    chip.setGpioController(gpioController);
                }

                chip.initialize();
                logger.info("initializeChipset: {}initialization of chip={} complete{}", ConsoleColours.GREEN, chip, ConsoleColours.RESET);
            }
            state = State.CHIPSET_INITIALIZED;
        } catch(ChipInitializationException e) {
            logger.warn("unit chipset initialization failed", e);
            state = FAILED;
        }
        logger.info("chipset created & initialized");
    }

    protected SPIConfiguration getSpiConfig() {
        return spiConfig;
    }

    @Override
    public State getState() {
        return state;
    }

    public abstract void initializeUnit() throws IOException;

    /**
     * Create chipset be inserting chips into the list.
     *
     * @param chipset chip list
     * @throws Exception
     */
    public abstract void createChipset(List<GenericChip> chipset);
}
