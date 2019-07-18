package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Ad5321;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.GenericDac;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8574;

import java.io.IOException;

@Component
public class FrontEndMixerUnit extends Unit implements HModeMixer, Reinitializable {
    private static final Logger logger = LoggerFactory.getLogger(FrontEndMixerUnit.class);

    private final int EXPANDER_ADDR = 0x3f;
    private final int DAC_SQUARER_ADDR = 0x0d;
    private final int DAC_BIAS_ADDR = 0x0c;

    private float biasPoint;
    private float squarer;
    private Mode mode;

    @Autowired
    public FrontEndMixerUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
        addToChipset(new Pcf8574(EXPANDER_ADDR));
        addToChipset(new Ad5321(DAC_SQUARER_ADDR));
        addToChipset(new Ad5321(DAC_BIAS_ADDR));
        initializeChipset();
    }

    @Override
    public void initializeUnit() throws Exception {
        Ad5321 adcBias = (Ad5321) getChip(DAC_BIAS_ADDR);
        Ad5321 adcSquarer = (Ad5321) getChip(DAC_SQUARER_ADDR);
        adcBias.setVoltage(0.0f);
        adcBias.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
        adcSquarer.setVoltage(0.0f);
        adcSquarer.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
    }

    @Override
    public void setBiasPoint(float voltage) throws Exception {
        logger.debug("setting mixer trafo bias point at {}V", voltage);
        GenericDac dac = (GenericDac) getChip(DAC_BIAS_ADDR);
        dac.setVoltage(voltage);
        this.biasPoint = voltage;
    }

    public float getBiasPoint() {
        return biasPoint;
    }

    @Override
    public void setSquarerThreshold(float percentage) throws Exception {
        logger.debug("setting squarer threshold at {}%", percentage);
        GenericDac dac = (GenericDac) getChip(DAC_SQUARER_ADDR);
        dac.setData((int) ((dac.getMaxData()*percentage)/100));
        this.squarer = percentage;
    }

    public float getSquarerThreshold() {
        return squarer;
    }

    @Override
    public void setRoofingFilter(Mode mode) throws IOException {
        logger.debug("setting roofing filter for {}", mode);
        Pcf8574 expander = (Pcf8574) getChip(EXPANDER_ADDR);
        switch(mode) {
            case CW:
                expander.writePort(1);
                break;
            case SSB:
                expander.writePort(0);
                break;
            default:
                throw new IllegalArgumentException("unrecognized mode: " + mode);
        }
        this.mode = mode;
    }

    public Mode getRoofingFilter() {
        return mode;
    }
}
