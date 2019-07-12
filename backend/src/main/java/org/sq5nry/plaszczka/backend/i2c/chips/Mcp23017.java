package org.sq5nry.plaszczka.backend.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.sq5nry.plaszczka.backend.i2c.GenericChip;

import java.io.IOException;

public class Mcp23017 extends GenericChip  {
    public static final byte IODIR_A = 0x00;
    public static final byte IODIR_B = 0x01;
    public static final byte IODIR_ALL_OUTPUTS = 0x00;

    public Mcp23017(I2CBus bus, int address) {
        super(bus, address);
    }

    public void writePort(Mcp23017.Port port, byte value) throws IOException {
        getDevice().write(port.getAddress(), value);
    }

    public enum Port {
        GPIO_A(0x12), GPIO_B(0x13);

        int addr;

        Port(int addr) {
            this.addr = addr;
        }

        public byte getAddress() {
            return (byte) addr;
        }
    }
}
