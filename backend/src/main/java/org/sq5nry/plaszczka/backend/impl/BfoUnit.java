package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.synthesiser.FrequencyOscillator;
import org.sq5nry.plaszczka.backend.hw.chips.Si570;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;

import java.io.IOException;
import java.util.List;

@Component
public class BfoUnit extends Unit implements FrequencyOscillator {
    private static final Logger logger = LoggerFactory.getLogger(BfoUnit.class);

    private static final int SI570_ADDR = 0x55;

    private Si570 xo;

    @Autowired
    public BfoUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(xo = new Si570(SI570_ADDR));
    }

    @Override
    public void initializeUnit() throws Exception {
        //TODO ?
    }

    @Override
    public void setFrequency(int freq) throws IOException {
        logger.info("setFrequency: {}Hz", freq);
        xo.setFrequency(freq);
    }

    @Override
    public String getName() {
        return "BFO Local Oscillator Subsystem";
    }
}
