package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;

/**
 * The AD5306 is a quad 8-bit buffered voltage output DACs in 16-lead TSSOP packages that operate from a single 2.5 V
 * to 5.5 V supply, consuming 500 μA at 3 V. Their on-chip output amplifiers allow rail-to-rail output swing with a slew
 * rate of 0.7 V/μs. A 2-wire serial interface, which operates at clock rates up to 400 kHz, is used. This interface
 * is SMBus-compatible at VDD < 3.6 V. Multiple devices can be placed on the same bus.
 *
 * https://www.analog.com/en/products/ad5306.html
 */
public class Ad5306 extends GenericDac {
    public static final int MAX = 255;

    /**
     * 0: Output range for that DAC set at 0 V to VREF.
     * 1: Output range for that DAC set at 0 V to 2 VREF.
     */
    public static final byte CTRL_GAIN = (byte) 0x8;

    /**
     * 0: Reference input for that DAC is unbuffered.
     * 1: Reference input for that DAC is buffered.
     *
     * //TODO optional, not used in hw
     */
    public static final byte CTRL_BUF = 0x4;

    /**
     * 0: All DAC registers and input registers are filled with 0s on completion of the write sequence.
     * 1: Normal operation.
     */
    public static final byte CTRL_N_CLR = 0x2;

    /**
     * 0: On completion of the write sequence, all four DACs go into power-down mode.
     *    The DAC outputs enter a high impedance state.
     * 1: Normal operation.
     */
    public static final byte CTRL_N_PD = 0x1;

    public enum DacChannel {
        DAC_A(1), DAC_B(2), DAC_C(3), DAC_D(4);

        int ch;
        DacChannel(int ch) {
            this.ch = ch;
        }

        public int getValue() {
            return ch;
        }
    }
    public enum DacPointer {
        DAC_A((byte)0x1), DAC_B((byte)0x2),
        DAC_C((byte)0x4), DAC_D((byte)0x8);

        byte val;
        DacPointer(byte val) {
            this.val = val;
        }

        public static DacPointer fromInt(int val) {
            switch (val) {
                case 1: return DAC_A;
                case 2: return DAC_B;
                case 3: return DAC_C;
                case 4: return DAC_D;
                default: throw new IllegalArgumentException("no channel available: " + val);
            }
        }

        public byte byteValue() {
            return val;
        }
    }

    private float vRef; //TODO could be separate per channel, but HW ties all together

    /*
        The power-on state is
            • Normal operation
            • Reference inputs unbuffered
            • 0 V to VREF output range
            • Output voltage set to 0 V
        Both input and DAC registers are filled with 0s and remain so until a valid write sequence is made to the device.
    */
    private byte control = CTRL_N_PD;
    private byte[] buffer;


    public Ad5306(I2CBus i2CBus, int address) {
        super(i2CBus, address);
        buffer = new byte[4];
    }

    @Override
    public float getVref() {
        return vRef;
    }

    public void setVRef(float vRef) {
        this.vRef = vRef;
    }

    @Override
    public int getMaxData() {
        return MAX;
    }

    @Override
    public void setData(int data, int channel) throws Exception {
        if (data<0 || data>MAX) {
            throw new IllegalArgumentException("DAC data out of 0..MAX range");
        }
        buffer[0] = (byte) ((data & 0x000F) << 4);
        buffer[1] = (byte) ((control << 4) | ((data & 0x000F) >> 4));
        getDevice().write(DacPointer.fromInt(channel).byteValue(), buffer);
    }

    @Override
    public void setData(int data) throws Exception {
        throw new UnsupportedOperationException("multi-channel device");
    }
}
