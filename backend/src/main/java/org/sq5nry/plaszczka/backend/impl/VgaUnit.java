package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.hw.chips.*;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.impl.common.BaseUnit;
import org.sq5nry.plaszczka.backend.impl.common.VParam;

import java.io.IOException;
import java.util.List;

@Component
public class VgaUnit extends BaseUnit implements IfAmp {
    private static final Logger logger = LoggerFactory.getLogger(VgaUnit.class);

    private final int DAC_IC18 = 0x0e;
    private final int DAC_IC19 = 0x0f;
    private final int DAC = 0x2f;
    private final int RDAC = 0x2f;
    private final int ADC = 0x29;

    @Autowired
    public VgaUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(new Ad5306(DAC_IC18));
        chipset.add(new Ad5306(DAC_IC19));
        chipset.add(new Ad5321(DAC));
        chipset.add(new Ad7999(ADC));
        chipset.add(new Ad5242(RDAC));
    }

    @Override
    public void initializeUnit() throws IOException {
        ((Ad5306) getChip(DAC_IC18)).setVRef(3.894f);
        ((Ad5306) getChip(DAC_IC19)).setVRef(5.0f);
        ((Ad7999) getChip(ADC)).configure();
    }

    /**
     * @param speed ms/131dB
     */
    @Override
    public void setDecaySpeedInDecayStateForHangMode(float speed) throws IOException {
        float data = VParam.VP.Vsph.getParam().convertToVoltage(speed);
        logger.info("setDecaySpeedInDecayStateForHangMode: speed={}V for input={}ms/131dB", data, speed);
        ((GenericDac) getChip(DAC_IC19)).setVoltage(data, Ad5306.DacChannel.DAC_A.getValue());
    }

    /**
     * @param speed ms/131dB
     */
    @Override
    public void setDecaySpeedForAttackDecayMode(float speed) throws IOException {
        float data = VParam.VP.Vspa.getParam().convertToVoltage(speed);
        logger.info("setDecaySpeedForAttackDecayMode: speed={}V for input={}ms/131dB", data, speed);
        ((GenericDac) getChip(DAC_IC19)).setVoltage(data, Ad5306.DacChannel.DAC_B.getValue());
    }

    /**
     * @param speed dB/s
     */
    @Override
    public void setDecaySpeedInHangStateForHangMode(float speed) throws IOException {
        float data = VParam.VP.Vleak.getParam().convertToVoltage(speed);
        logger.info("setDecaySpeedInHangStateForHangMode: speed={}V for input={}dB/s", data, speed);
        ((GenericDac) getChip(DAC_IC19)).setVoltage(data, Ad5306.DacChannel.DAC_C.getValue());
    }

    /**
     * @param val dB
     */
    @Override
    public void setNoiseFloorCompensation(float val) throws IOException {
        float data = VParam.VP.Vfloor.getParam().convertToVoltage(val);
        logger.info("setNoiseFloorCompensation: {}V for input={}dB", data, val);
        ((GenericDac) getChip(DAC_IC19)).setVoltage(data, Ad5306.DacChannel.DAC_D.getValue());
    }

    @Override
    public void setStrategyThreshold(float val) throws IOException {
        float data = VParam.VP.Vath.getParam().convertToVoltage(val);
        logger.info("setStrategyThreshold: {}V for input={}dBm", data, val);
        ((GenericDac) getChip(DAC_IC18)).setVoltage(data, Ad5306.DacChannel.DAC_A.getValue());
    }

    @Override
    public void setHangThreshold(float val) throws IOException {  //TODO 1 bit off for lower values
        float data = VParam.VP.Vhth.getParam().convertToVoltage(val);
        logger.info("setHangThreshold: {}V for input={}dB", data, val);
        ((GenericDac) getChip(DAC_IC18)).setVoltage(data, Ad5306.DacChannel.DAC_B.getValue());
    }

    @Override
    public void setVLoop(float val) throws IOException {
        float data = VParam.VP.Vloop.getParam().convertToVoltage(val);
        logger.info("setVLoop: {}V for input={}dB", data, val);
        ((GenericDac) getChip(DAC_IC18)).setVoltage(data, Ad5306.DacChannel.DAC_C.getValue());
    }

    @Override
    public void setMaximumGain(float gain) throws IOException {
        float data = VParam.VP.Vgain.getParam().convertToVoltage(gain);
        logger.info("setMaximumGain: {}V for input={}dB", data, gain);
        ((GenericDac) getChip(DAC_IC18)).setVoltage(data, Ad5306.DacChannel.DAC_D.getValue());
    }

    @Override
    public void setMaximumHangTimeInHangMode(float val) throws IOException {
        float data = VParam.VP.Vspd.getParam().convertToVoltage(val);
        logger.info("setMaximumHangTimeInHangMode: {}V for input={}ms", data, val);
        data *= 256f/5f;   //TODO const or extend RDAC with GenericDac capability
        ((Ad5242) getChip(RDAC)).setData((int) data, Ad5242.Rdac.RDAC2);
    }

    @Override
    public void setAttackTime(float val) throws IOException {
        float data = VParam.VP.Attack.getParam().convertToVoltage(val); //TODO resistance, not voltage
        logger.info("setAttackTime: {}kOhm for input={}ms", data, val);
        data *= 256f/100f;   //TODO const or extend RDAC with ohmic cap.
        ((Ad5242) getChip(RDAC)).setData((int) data, Ad5242.Rdac.RDAC1);
    }

    @Override
    public void setHangOnTransmit(boolean enabled) throws IOException {
        logger.info("setHangOnTransmit: {}", enabled);
        ((Ad5242) getChip(RDAC)).setOutPin(enabled, Ad5242.OutPin.O1);
    }

    @Override
    public void setMute(boolean enabled) throws IOException {
        logger.debug("setMute: {}", enabled);
        ((Ad5242) getChip(RDAC)).setOutPin(enabled, Ad5242.OutPin.O2);
    }

    @Override
    public int getVAgc() throws IOException {
        byte vagc = ((Ad7999) getChip(ADC)).getConversionResult();
        if (logger.isTraceEnabled()) {
            logger.trace("getVAgc: 0x{}", String.format("%02X", vagc));
        }
        return vagc & 0xFF;
    }

    @Override
    public String getName() {
        return "IF Subsystem: Variable Gain Board";
    }
}
