package org.sq5nry.plaszczka.backend.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.apache.commons.lang3.BitField;

public class Pcf8575 extends Pcf8574 {
    public BitField P1 = new BitField(0xFF00);

    public Pcf8575(I2CBus bus, int address) {
        super(bus, address);
    }
}
