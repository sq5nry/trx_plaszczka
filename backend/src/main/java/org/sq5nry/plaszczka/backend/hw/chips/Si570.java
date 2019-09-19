package org.sq5nry.plaszczka.backend.hw.chips;

import com.google.common.primitives.Longs;
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
 *
 * This driver implements only a narrow output range of 31-36.8MHz as needed by BFO Unit.
 * For a wider range, HSDIV and N1 coefficients must be estimated and also coarse re-tuning procedure applied.
 */
public class Si570 extends GenericI2CChip {
    private static final Logger logger = LoggerFactory.getLogger(Si570.class);

    /**
     * I2C registers
     */
    private static final int REG_HS_N1 = 7;
    private static final int REG_N1_RF_01 = 8;
    private static final int REG_RF_02 = 9;
    private static final int REG_RF_03 = 10;
    private static final int REG_RF_04 = 11;
    private static final int REG_RF_05 = 12;
    private static final int REG_RES_FRE_MEMCTL = 135;
    private static final int REG_FREEZE_DCO = 137;

    /**
     * Initial actually measured output frequency in Hz
     */
    private static final long F0_HZ = 9999951; //TODO config
    private static final double FRACT_LEN = 1 << 28;

    /**
     * Internal DCO frequency range
     */
    private static final long FDCO_MIN_HZ = 4850000000L;
    private static final long FDCO_MAX_HZ = 5670000000L;

    private static final int HS_4 = 0x0;
    private static final int HS_5 = 0x1;
    private static final int HS_6 = 0x2;
    private static final int HS_7 = 0x3;
    private static final int HS_9 = 0x5;
    private static final int HS_11 = 0x7;
    private static final int[] DIV_TO_HS = {0, 0, 0, 0, HS_4, HS_5, HS_6, HS_7, 0, HS_9, 0, HS_11};
    private static final int[] HS_TO_DIV = {4, 5, 6, 7, 0, 9, 0, 11};

    // these dividers establish 31-36.8MHz fixed output range
    private static int ASSUMED_HS = HS_11;
    private static int ASSUMED_N1DIV = 14;

    // precisely calculated available output frequency range
    private static long OUT_MIN_F_HZ;
    private static long OUT_MAX_F_HZ;

    private static int INITIAL_FREQ = 36000000;

    private int dcoHighSpeedDivider;
    private int clkoutOutputDivider;
    private double FXTAL_HZ;

    public Si570(int address) {
        super(address, "Si570");
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
            final int hsN1 = getDevice().read(REG_HS_N1);
            dcoHighSpeedDivider = HS_TO_DIV[hsN1 >> 5];
            logger.info("DCO High Speed Divider={}", dcoHighSpeedDivider);

            final byte n1Rf = (byte) getDevice().read(REG_N1_RF_01);
            clkoutOutputDivider = getN1DividerSetting(hsN1, n1Rf);
            logger.info("CLK OUT Output Divider={}", clkoutOutputDivider);

            final long rfr = calculateRfreq(n1Rf);  //12 000 000 000  around +/-1 000 000 000

            final double rfreq = (double) rfr / FRACT_LEN;
            logger.info("RFREQ={}", rfreq);

            FXTAL_HZ = (F0_HZ * dcoHighSpeedDivider * clkoutOutputDivider * FRACT_LEN) / rfr;
            logger.info("actual crystal frequency={}[Hz], DCO running at {}[Hz]", FXTAL_HZ, FXTAL_HZ * rfreq);

            OUT_MIN_F_HZ = FDCO_MIN_HZ / (HS_TO_DIV[ASSUMED_HS] * ASSUMED_N1DIV);
            OUT_MAX_F_HZ = FDCO_MAX_HZ / (HS_TO_DIV[ASSUMED_HS] * ASSUMED_N1DIV);
            logger.info("supported output frequency range {}-{}[Hz]", OUT_MIN_F_HZ, OUT_MAX_F_HZ);

            //for other frequency ranges, these divider values must be established
            clkoutOutputDivider = ASSUMED_N1DIV;
            dcoHighSpeedDivider = HS_TO_DIV[ASSUMED_HS];
            setFrequency0(INITIAL_FREQ, false);
        } catch (IOException e) {
            throw new ChipInitializationException("failed to read registers for initialization", e);
        }
        return this;
    }

    /**
     * Set frequency in Hz
     * @param freq Hz
     */
    public void setFrequency(long freq) throws IOException {
        if (OUT_MIN_F_HZ > freq || freq > OUT_MAX_F_HZ) {
            throw new IllegalArgumentException("input frequency out of range");
        }
        setFrequency0(freq, true);
    }

    /**
     * Set frequency in Hz
     * @param freq Hz
     * @param isSmallChange +/-3500ppm
     * @throws IOException
     */
    private void setFrequency0(long freq, boolean isSmallChange) throws IOException {
        logger.debug("setFrequency: {}Hz", freq);
        final long fdco = freq * dcoHighSpeedDivider * clkoutOutputDivider;
        if (fdco < FDCO_MIN_HZ || fdco > FDCO_MAX_HZ) {
            throw new IllegalArgumentException("fDCO outside valid range: " + fdco);
        }
        long newRfreq = (long) ((fdco / FXTAL_HZ) * FRACT_LEN);

        logger.debug("newRfreq={}", newRfreq);
        byte[] newRfData = Longs.toByteArray(newRfreq);
        logger.debug("setFrequency: fDCO={}[Hz], newRfreq={}", fdco, newRfreq);

        writeRegisters(newRfData, isSmallChange);
        logger.debug("setFrequency: completed");
    }

    /**
     * Updates registers.
     * Divider settings taken from global variables //TODO
     *
     * @param newRfData fractional divider setting
     * @param isSmallChange within +/-3500ppm
     * @throws IOException
     */
    private void writeRegisters(byte[] newRfData, boolean isSmallChange) throws IOException {
        if (isSmallChange){
            write(REG_RES_FRE_MEMCTL, (byte) 0x20); // 135@FreezeM
        } else {
            write(REG_FREEZE_DCO, (byte) 0x10);     // 137&FreezeDCO
        }

        write(REG_HS_N1, (byte) ((DIV_TO_HS[dcoHighSpeedDivider] << 5) | ((clkoutOutputDivider-1) >> 2)));
        write(REG_N1_RF_01, (byte) (((clkoutOutputDivider-1) << 6) | (newRfData[3] & 0x3F)));
        write(REG_RF_02, newRfData[4]);
        write(REG_RF_03, newRfData[5]);
        write(REG_RF_04, newRfData[6]);
        write(REG_RF_05, newRfData[7]);

        if (isSmallChange){
            write(REG_RES_FRE_MEMCTL, (byte) 0x00); // 135@Un-FreezeM
        } else {
            write(REG_FREEZE_DCO, (byte) 0x00);     // 137&Un-FreezeDCO
            write(REG_RES_FRE_MEMCTL, (byte) 0x40); // 135@NewFreq
        }
    }

    private void write(int reg, byte data) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("write: reg={}, data=0x{}", reg, String.format("%02X", data));
        }
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

    private long calculateRfreq(byte n1Rf) throws IOException {
        long rfreqRaw;
        rfreqRaw = (byte) (n1Rf & 0x3F);
        rfreqRaw = (rfreqRaw << 8) + getDevice().read(REG_RF_02);
        rfreqRaw = (rfreqRaw << 8) + getDevice().read(REG_RF_03);
        rfreqRaw = (rfreqRaw << 8) + getDevice().read(REG_RF_04);
        rfreqRaw = (rfreqRaw << 8) + getDevice().read(REG_RF_05);
        return rfreqRaw;
    }

    private static int getN1DividerSetting(int hsN1, byte n1Rf) {
        int div = ((hsN1 & 0x1F) << 2) + ((n1Rf & 0x1F) >> 6) + 1;
        if (1 == (div & 1)) {
            div++;
        }
        return div;
    }
}