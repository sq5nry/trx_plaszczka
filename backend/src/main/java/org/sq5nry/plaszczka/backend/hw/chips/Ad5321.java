package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The AD5321 is a single 12-bit, buffered, voltage-output DACs that operate from a single 2.5 V to 5.5 V supply,
 * consuming 120 μA at 3 V. The on-chip output amplifier allows rail-to-rail output swing with a slew rate of 0.7 V/μs.
 * It uses a 2-wire (I2 C-compatible) serial interface that operates at clock rates up to 400 kHz. Multiple devices can
 * share the same bus.
 *
 * The reference for the DAC is derived from the power supply inputs and thus gives the widest dynamic output range.
 * These devices incorporate a power-on reset circuit, which ensures that the DAC output powers up to 0 V and remains
 * there until a valid write takes place. The devices contain a power-down feature that reduces the current consumption
 * of the device to 50 nA at 3 V and provides software-selectable output loads while in power-down mode.
 *
 * https://www.analog.com/en/products/ad5321.html
 */
public class Ad5321 extends GenericDac {
    private static final Logger logger = LoggerFactory.getLogger(Ad5321.class);

    public static final int MAX = 4095;
    public static final int VREF = 5;

    public enum PD_MODE {
        PD_NORMAL_OPERATION((byte)0x0), PD_POWER_DOWN_1K_TO_GND((byte)0x1),
        PD_POWER_DOWN_100K_TO_GND((byte)0x2), PD_POWER_DOWN_HI_Z((byte)0x3);

        byte val;
        PD_MODE(byte val) {
            this.val = val;
        }

        public byte byteValue() {
            return val;
        }
    }

    private PD_MODE pdMode;
    private int data;

    public Ad5321(int address) {
        super(address, "AD5321");
        pdMode = PD_MODE.PD_POWER_DOWN_HI_Z;
    }

    /**
     * Set Power Down mode
     * @param pdMode
     */
    public void setPDMode(PD_MODE pdMode) throws IOException {
        this.pdMode = pdMode;
        update();
    }

    @Override
    public float getVref() {
        return VREF;
    }

    /**
     * Sets DAC output value and commands an update, considering current Power Down mode.
     * @param data 0..4095
     */
    public void setData(int data) throws IOException {
        if (data<0 || data>MAX) {
            if (data>MAX) data = MAX;
            if (data<0) data = 0;
            logger.error("DAC data out of 0..MAX range, limiting!");    //TODO fix calculation roundings
            //throw new IllegalArgumentException("DAC data out of 0..MAX range");
        }
        this.data = data;
        update();   //TODO if fails, data value in object is untrue
    }

    private void update() throws IOException {
        byte[] buffer = {(byte) (pdMode.byteValue() | ((data & 0xFF00) >> 8)), (byte) (data & 0xFF)};
        getDevice().write(buffer);
    }

    @Override
    public int getMaxData() {
        return MAX;
    }

    @Override
    public void setData(int data, int channel) throws IOException {
        throw new UnsupportedOperationException("single channel only");
    }
}
