package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class VgaUnit implements IfAmp {
    private static final Logger logger = LoggerFactory.getLogger(VgaUnit.class);

    private final I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int DAC_IC18 = 0x0e;
    private final int DAC_IC19 = 0x0f;
    private final int DAC = 0x2f;
    private final int RDAC = 0x2f;
    private final int ADC = 0x29;

    @Autowired
    public VgaUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating chipset");
        bus = i2cBusProv.getBus();
        chipset.put(DAC_IC18, new Ad5306(bus, DAC_IC18).initialize());
        chipset.put(DAC_IC19, new Ad5306(bus, DAC_IC19).initialize());
        chipset.put(DAC, new Ad5321(bus, DAC).initialize());
        chipset.put(ADC, new Ad7999(bus, ADC).initialize());
        chipset.put(RDAC, new Ad5242(bus, ADC).initialize());
        initialize();
        logger.debug("chipset created & initialized");
    }

    private void initialize() {
        Ad5306 dac18 = (Ad5306) chipset.get(DAC_IC18);
        dac18.setVRef(3.894f);
        Ad5306 dac19 = (Ad5306) chipset.get(DAC_IC19);
        dac19.setVRef(5.0f);
    }

    @Override
    //32
    public void setDecaySpeedInDecayStateForHangMode(int speed) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC19);
        dac.setData(speed, Ad5306.DacChannel.DAC_A.getValue());
    }

    @Override
    //8
    public void setDecaySpeedForAttackDecayMode(int speed) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC19);
        dac.setData(speed, Ad5306.DacChannel.DAC_B.getValue());
    }

    @Override
    //4
    public void setDecaySpeedInHangStateForHangMode(int speed) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC19);
        dac.setData(speed, Ad5306.DacChannel.DAC_C.getValue());
    }

    @Override
    // 0x0f 0x08 0x9037 w ???
    //151?
    public void setNoiseFloorCompensation(int val) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC19);
        dac.setData(val, Ad5306.DacChannel.DAC_D.getValue());
    }

    @Override
    //112
    public void setStrategyThreshold(int val) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC18);
        dac.setData(val, Ad5306.DacChannel.DAC_A.getValue());
    }

    @Override
    //20
    public void setHangThreshold(int val) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC18);
        dac.setData(val, Ad5306.DacChannel.DAC_B.getValue());
    }

    @Override
    //12
    public void setVLoop(int val) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC18);
        dac.setData(val, Ad5306.DacChannel.DAC_C.getValue());
    }

    @Override
    //0
    public void setMaximumGain(int gain) throws Exception {
        GenericDac dac = (GenericDac) chipset.get(DAC_IC18);
        dac.setData(gain, Ad5306.DacChannel.DAC_C.getValue());
    }

    @Override
    //15
    public void setMaximumHangTimeInHangMode(int val) throws Exception {
        Ad5242 rdac = (Ad5242) chipset.get(RDAC);
        rdac.setData(val, Ad5242.Rdac.RDAC2);
    }

    @Override
    //10
    public void setAttackTime(int val) throws Exception {
        Ad5242 rdac = (Ad5242) chipset.get(RDAC);
        rdac.setData(val, Ad5242.Rdac.RDAC1);
    }

    @Override
    public void setHangOnTransmit(boolean enabled) throws Exception {
        Ad5242 rdac = (Ad5242) chipset.get(RDAC);
        rdac.setOutPin(enabled, Ad5242.OutPin.O1);
    }

    @Override
    public void setMute(boolean enabled) throws Exception {
        Ad5242 rdac = (Ad5242) chipset.get(RDAC);
        rdac.setOutPin(enabled, Ad5242.OutPin.O2);
    }
}
