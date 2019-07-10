package org.sq5nry.plaszczka.backend.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.sq5nry.plaszczka.backend.i2c.GenericChip;

public class Ad7999 extends GenericChip {
    public Ad7999(I2CBus bus, int address) {
        super(bus, address);
    }
}
