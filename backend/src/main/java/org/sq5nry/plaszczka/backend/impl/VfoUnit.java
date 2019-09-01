package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.synthesiser.FrequencyOscillator;
import org.sq5nry.plaszczka.backend.hw.chips.Ad9954;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.gpio.GpioControllerProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.spi.SPIConfiguration;

import java.io.IOException;
import java.util.List;

@Component
public class VfoUnit extends Unit implements FrequencyOscillator {
    private static final Logger logger = LoggerFactory.getLogger(VfoUnit.class);

    private Ad9954 dds;

    //TODO nie chce i2c
    @Autowired
    public VfoUnit(I2CBusProvider i2cBusProv, SPIConfiguration spiConfig, GpioControllerProvider gpioCtlProvider) throws Exception {
        super(i2cBusProv, spiConfig, gpioCtlProvider);
    }

    @Override
    public void setFrequency(int freq) throws IOException {
        logger.info("setFrequency: {}Hz", freq);
        dds.setFrequency(freq);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) throws Exception {
        logger.info("createChipset: entering");
        chipset.add(dds = new Ad9954(getSpiConfig(), 500000000));
        logger.info("createChipset: DDS={}", dds);
    }

    @Override
    public String getName() {
        return "VFO Local Oscillator Subsystem";
    }
}