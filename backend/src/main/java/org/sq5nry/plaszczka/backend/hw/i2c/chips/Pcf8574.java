package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.apache.commons.lang3.BitField;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

import java.io.IOException;

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
        return "Pcf8574{" + Integer.toHexString(getAddress()) + "}";
    }
}
