package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

import java.io.IOException;

/**
 * The AD5241/AD5242 provide a single-/dual-channel, 256-position, digitally controlled variable resistor (VR) device.
 * Thesedevices perform the same electronic adjustment function as a potentiometer, trimmer, or variable resistor.
 * Each VR offers a completely programmable value of resistance between the A terminal and the wiper, or the B terminal
 * and the wiper. For the AD5242, the fixed A-to-B terminal resistance of 10 kΩ, 100 kΩ, or 1 MΩ has a 1% channel-to-channel
 * matching tolerance.
 *
 * https://www.analog.com/en/products/ad5242.html
 */
public class Ad5242 extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(Ad5242.class);

    public static final int MAX = 255;

    public static final byte RDAC_MASK = (byte) 0x80;
    public static final byte MIDSCALE_RESET_MASK = 0x40;
    public static final byte SHUTDOWN_MASK = 0x20;

    private boolean o1;
    private boolean o2;
    private boolean shutdown;
    private boolean midscaleReset;
    private Rdac rdac;
    private byte data;
    private byte instruction;

    private byte[] buffer = new byte[2];

    public enum Rdac {
        RDAC1, RDAC2;
    }

    public enum OutPin {
        O1((byte)0x10), O2((byte)0x08);

        byte val;
        OutPin(byte val) {
            this.val = val;
        }

        public byte getMask() {
            return val;
        }
    }


    public Ad5242(int address) {
        super(address);
    }

    public void setData(int data, Rdac rdac) throws IOException {
        if (data<0 || data>255) {
            if (data>MAX) data = MAX;
            if (data<0) data = 0;
            logger.error("DAC data out of 0..MAX range, limiting!");    //TODO fix calculation roundings
            //throw new IllegalArgumentException("RDAC data outside of range 0..255: " + data);
        }
        this.data = (byte) data;
        this.rdac = rdac;
        update();
    }

    public void setOutPin(boolean val, OutPin pin) throws IOException {
        if (OutPin.O1 == pin) {
            o1 = val;
        } else if (OutPin.O2 == pin) {
            o2 = val;
        } else {
            throw new IllegalArgumentException("unknown out pin: " + pin);
        }
        update();
    }

    public void setShutdown(boolean enabled) throws IOException {
        this.shutdown = enabled;
        update();
    }

    private void update() throws IOException {
        instruction = 0x00;
        if (shutdown) instruction |= SHUTDOWN_MASK;
        if (o1) instruction |= OutPin.O1.getMask();
        if (o2) instruction |= OutPin.O2.getMask();
        if (rdac == Rdac.RDAC2) instruction |= RDAC_MASK;
        if (midscaleReset) instruction |= MIDSCALE_RESET_MASK;

        buffer[0] = instruction;
        buffer[1] = data;
        logger.debug("update: instruction={}, data={}", String.format("%02X", instruction), String.format("%02X", data));
        getDevice().write(buffer);
    }
}
