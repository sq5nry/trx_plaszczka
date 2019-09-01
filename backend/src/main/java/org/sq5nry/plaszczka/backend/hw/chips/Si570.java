package org.sq5nry.plaszczka.backend.hw.chips;

import com.google.common.primitives.Longs;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2CChip;

import java.io.IOException;

/**
 * The Si570 XO/Si571 VCXO utilizes Silicon Laboratories’ advanced DSPLL®
 * circuitry to provide a low-jitter clock at any frequency. The Si570/Si571 are user-programmable
 * to any output frequency from 10 to 945 MHz and select frequencies
 * to 1400 MHz with <1 ppb resolution. The device is programmed via an I2C serial
 * interface. Unlike traditional XO/VCXOs where a different crystal is required for
 * each output frequency, the Si57x uses one fixed-frequency crystal and a DSPLL
 * clock synthesis IC to provide any-frequency operation. This IC-based approach
 * allows the crystal resonator to provide exceptional frequency stability and
 * reliability. In addition, DSPLL clock synthesis provides superior supply noise
 * rejection, simplifying the task of generating low-jitter clocks in noisy environments
 * typically found in communication systems.
 */
public class Si570 extends GenericI2CChip {
    private static final Logger logger = LoggerFactory.getLogger(Si570.class);

    private static final int REG_HS_N1 = 7;
    private static final int REG_N1_RF_01 = 8;
    private static final int REG_RF_02 = 9;
    private static final int REG_RF_03 = 10;
    private static final int REG_RF_04 = 11;
    private static final int REG_RF_05 = 12;
    private static final int REG_RES_FRE_MEMCTL = 135;
    private static final int REG_FREEZE_DCO = 137;

    private static final double F0 = 9.999951d; //TODO config
    private static final long FRACT_LEN = 1 << 28;

    private static final float FDCO_MIN_GHZ = 4850;
    private static final float FDCO_MAX_GHZ = 5670;


    private int dcoHighSpeedDivider;
    private int clkoutOutputDivider;
    private double rfreq;
    private double fxtal;

    public Si570(int address) {
        super(address);
    }

    @Override
    public GenericChip initialize() throws ChipInitializationException {
        //check presence on the bus
        super.initialize();

        //reset initial chip registers
        reset();

        //read initial oscillator conditions
        logger.debug("initializing Si570");
        try {
            int hsN1 = getDevice().read(REG_HS_N1);
            switch (hsN1 >> 5) {
                case 0x0: dcoHighSpeedDivider = 4; break;
                case 0x1: dcoHighSpeedDivider = 5; break;
                case 0x2: dcoHighSpeedDivider = 6; break;
                case 0x3: dcoHighSpeedDivider = 7; break;
                case 0x4: case 0x6: throw new ChipInitializationException("DCO High Speed Divider value not used: " + hsN1);
                case 0x5: dcoHighSpeedDivider = 9; break;
                case 0x7: dcoHighSpeedDivider = 11; break;
            }
            logger.info("DCO High Speed Divider={}", dcoHighSpeedDivider);

            byte n1Rf = (byte) getDevice().read(REG_N1_RF_01);
            clkoutOutputDivider = ((hsN1 & 0x1F) << 2) + (n1Rf >> 6);
            if (1 == (clkoutOutputDivider & 1)) {
                clkoutOutputDivider++;
            }
            logger.info("CLK OUT Output Divider={}", clkoutOutputDivider);

            long rfreqRaw = Longs.fromByteArray(new byte[]{0,0,0,
                    (byte) (n1Rf & 0x3F),
                    (byte) getDevice().read(REG_RF_02),
                    (byte) getDevice().read(REG_RF_03),
                    (byte) getDevice().read(REG_RF_04),
                    (byte) getDevice().read(REG_RF_05)});
            logger.info("raw RFREQ={}", rfreqRaw);

            rfreq = (double) rfreqRaw / FRACT_LEN;
            logger.info("RFREQ={}MHz", rfreq);

            fxtal = F0 * dcoHighSpeedDivider * clkoutOutputDivider / rfreq;
            logger.info("actual nominal crystal frequency fXtal={}MHz", fxtal);
        } catch (IOException e) {
            throw new ChipInitializationException("failed to read registers for initialization", e);
        }
        return this;
    }

    /**
     * Set frequency in Hz
     * @param freq Hz
     */
    public void setFrequency(int freq) throws IOException {
        clkoutOutputDivider = 16;   //TODO calc

        logger.debug("setFrequency: {}Hz", freq);
        double fdco = (freq / 1000000) * dcoHighSpeedDivider * clkoutOutputDivider;
        if (fdco < FDCO_MIN_GHZ || fdco > FDCO_MAX_GHZ) {
            throw new IllegalArgumentException("fDCO outside valid range: " + fdco);
        }
        long newRfreq = (long) ((fdco / fxtal) * FRACT_LEN);

        byte[] newRfData = Longs.toByteArray(newRfreq);
        logger.debug("setFrequency: fDCO={}MHz, newRfreq=0x{}", fdco, HexUtils.toHexString(newRfData));

        write(REG_FREEZE_DCO, (byte) 0x10);

        write(REG_HS_N1, (byte) 0xA4);    //TODO calc
        write(REG_N1_RF_01, (byte) ((clkoutOutputDivider << 6) | (newRfData[3] & 0x3F)));
        write(REG_RF_02, newRfData[4]);
        write(REG_RF_03, newRfData[5]);
        write(REG_RF_04, newRfData[6]);
        write(REG_RF_05, newRfData[7]);

        write(REG_FREEZE_DCO, (byte) 0x00);
        write(REG_RES_FRE_MEMCTL, (byte) 0x40);    //TODO readfirst
        logger.debug("setFrequency: completed");
    }

    private void write(int reg, byte data) throws IOException {
        logger.debug("write: reg={}, data=0x{}", reg, String.format("%02X", data));
        getDevice().write(reg, data);
    }

    public void reset() throws ChipInitializationException {
        logger.info("resetting...");
        int reg;
        try {
            getDevice().write(REG_RES_FRE_MEMCTL, (byte) 0x01);
            reg = getDevice().read(REG_RES_FRE_MEMCTL);
        } catch (IOException e) {
            throw new ChipInitializationException("reset failed", e);
        }

        if (1 == (reg & 1)) {
            throw new ChipInitializationException("reset timeout");
        }
    }
}