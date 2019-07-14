package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.apache.commons.lang3.BitField;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

import java.io.IOException;

/**
 * The PCF8574/74A provides general-purpose remote I/O expansion via the two-wire bidirectional I2C-bus
 * (serial clock (SCL), serial data (SDA)).The devices consist of eight quasi-bidirectional ports, 100 kHz I2C-bus
 * interface, three hardware address inputs and interrupt output operating between 2.5 V and 6 V. The quasi-bidirectional
 * port can be independently assigned as an input to monitor interrupt status or keypads, or as an output to activate
 * indicator devices such as LEDs. System master can read from the input port or write to the output port through
 * a single register.
 */
public class Pcf8574 extends GenericChip {
    public Pcf8574(I2CBus bus, int address) {
        super(bus, address);
    }

    /**
     * Read data from port.
     * @return 0..255
     * @throws IOException
     */
    public int readPort() throws IOException {
        return getDevice().read();
    }

    /**
     * Read data from port.
     * @return 0..255
     * @throws IOException
     */
    public int readPort(BitField port) throws IOException {
        return port.getValue(getDevice().read());
    }

    /**
     * Write data to port.
     * @param data 0..255
     * @throws IOException
     */
    public void writePort(int data) throws IOException {
        getDevice().write((byte) data);
    }

    @Override
    public String toString() {
        return "PCF8574{" + Integer.toHexString(getAddress()) + "}";
    }
}
