package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.hw.chips.Ad5321;
import org.sq5nry.plaszczka.backend.hw.chips.GenericDac;
import org.sq5nry.plaszczka.backend.hw.chips.Pcf8574;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.impl.common.BaseUnit;

import java.io.IOException;
import java.util.List;

@Component
public class FrontEndMixerUnit extends BaseUnit implements HModeMixer {
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
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(new Pcf8574(EXPANDER_ADDR));
        chipset.add(new Ad5321(DAC_SQUARER_ADDR));
        chipset.add(new Ad5321(DAC_BIAS_ADDR));
    }

    @Override
    public void initializeUnit() throws IOException {
        Ad5321 adcBias = (Ad5321) getChip(DAC_BIAS_ADDR);
        Ad5321 adcSquarer = (Ad5321) getChip(DAC_SQUARER_ADDR);
        adcBias.setVoltage(0.0f);
        adcBias.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
        adcSquarer.setVoltage(0.0f);
        adcSquarer.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
    }

    @Override
    public void setBiasPoint(float voltage) throws IOException {
        logger.info("setting mixer trafo bias point at {}V", voltage);
        GenericDac dac = (GenericDac) getChip(DAC_BIAS_ADDR);
        dac.setVoltage(voltage);
        this.biasPoint = voltage;
    }

    public float getBiasPoint() {
        return biasPoint;
    }

    @Override
    public void setSquarerThreshold(float percentage) throws IOException {
        logger.info("setting squarer threshold at {}%", percentage);
        GenericDac dac = (GenericDac) getChip(DAC_SQUARER_ADDR);
        dac.setData((int) ((dac.getMaxData()*percentage)/100));
        this.squarer = percentage;
    }

    public float getSquarerThreshold() {
        return squarer;
    }

    @Override
    public void setRoofingFilter(Mode mode) throws IOException {
        logger.info("setting roofing filter for {}", mode);
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

    @Override
    public String getName() {
        return "Frontend Board: H-Mode Mixer & Roofing Filters";
    }
}
