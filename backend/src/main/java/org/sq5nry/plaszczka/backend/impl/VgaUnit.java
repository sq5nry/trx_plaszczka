package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Ad5306;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Ad5321;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Ad7999;

import java.util.HashMap;
import java.util.Map;

@Component
public class VgaUnit implements IfAmp {
    private static final Logger logger = LoggerFactory.getLogger(VgaUnit.class);

    private final I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int DAC_1 = 0x0e;
    private final int DAC_2 = 0x0f;
    private final int DAC_3 = 0x2f;
    private final int ADC = 0x29;

    /*
    AD5306 0e
    AD5306 0f
    AD7999 29
    AD5242 2f
     */

    @Autowired
    public VgaUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating chipset");
        bus = i2cBusProv.getBus();
        chipset.put(DAC_1, new Ad5306(bus, DAC_1).initialize());
        chipset.put(DAC_2, new Ad5306(bus, DAC_2).initialize());
        chipset.put(DAC_3, new Ad5321(bus, DAC_3).initialize());
        chipset.put(ADC, new Ad7999(bus, ADC).initialize());

        //initialize();
        logger.debug("chipset created & initialized");
    }

    @Override
    public void setDecaySpeedInDecayStateForHangMode(int speed) {

    }

    @Override
    public void setDecaySpeedForAttackDecayMode(int speed) {

    }

    @Override
    public void setDecaySpeedInHangStateForHangMode(int speed) {

    }

    @Override
    public void setNoiseFloorCompensation(int val) {

    }

    @Override
    public void setStrategyThreshold(int val) {

    }

    @Override
    public void setHangThreshold(int val) {

    }

    @Override
    public void setVLoop(int val) {

    }

    @Override
    public void setMaximumGain(int gain) {

    }

    @Override
    public void setMaximumHangTimeInHangMode(int val) {

    }

    @Override
    public void setAttackTime(int val) {

    }

    @Override
    public void setHangOnTransmit(boolean enabled) {

    }

    @Override
    public void setMute(boolean enabled) {

    }
}
