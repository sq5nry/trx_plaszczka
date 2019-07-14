package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Ad5321;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.GenericDac;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8574;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FrontEndMixerUnit implements HModeMixer, Reinitializable {
    private static final Logger logger = LoggerFactory.getLogger(FrontEndMixerUnit.class);

    private final I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int EXPANDER_ADDR = 0x3f;
    private final int DAC_SQUARER_ADDR = 0x0d;
    private final int DAC_BIAS_ADDR = 0x0c;

    private float biasPoint;
    private float squarer;
    private Mode mode;

    @Autowired
    public FrontEndMixerUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating chipset");
        bus = i2cBusProv.getBus();
        Pcf8574 expander = new Pcf8574(bus, EXPANDER_ADDR);
        expander.initialize();
        chipset.put(EXPANDER_ADDR, expander);
        chipset.put(DAC_SQUARER_ADDR, new Ad5321(bus, DAC_SQUARER_ADDR).initialize());
        chipset.put(DAC_BIAS_ADDR, new Ad5321(bus, DAC_BIAS_ADDR).initialize());
        initialize();
        logger.debug("chipset created & initialized");
    }

    @Override
    public void initialize() throws Exception {
        Ad5321 adcBias = (Ad5321) chipset.get(DAC_BIAS_ADDR);
        Ad5321 adcSquarer = (Ad5321) chipset.get(DAC_SQUARER_ADDR);
        adcBias.setVoltage(0.0f);
        adcBias.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
        adcSquarer.setVoltage(0.0f);
        adcSquarer.setPDMode(Ad5321.PD_MODE.PD_NORMAL_OPERATION);
    }

    @Override
    public void setBiasPoint(float voltage) throws Exception {
        logger.debug("setting mixer trafo bias point at {}V", voltage);
        GenericDac dac = (GenericDac) chipset.get(DAC_BIAS_ADDR);
        dac.setVoltage(voltage);
        this.biasPoint = voltage;
    }

    public float getBiasPoint() {
        return biasPoint;
    }

    @Override
    public void setSquarerThreshold(float percentage) throws Exception {
        logger.debug("setting squarer threshold at {}%", percentage);
        GenericDac dac = (GenericDac) chipset.get(DAC_SQUARER_ADDR);
        dac.setData((int) ((dac.getMaxData()*percentage)/100));
        this.squarer = percentage;
    }

    public float getSquarerThreshold() {
        return squarer;
    }

    @Override
    public void setRoofingFilter(Mode mode) throws IOException {
        logger.debug("setting roofing filter for {}", mode);
        Pcf8574 expander = (Pcf8574) chipset.get(EXPANDER_ADDR);
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
