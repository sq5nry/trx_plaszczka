package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2cChip;

public class Si570 extends GenericI2cChip {
    private static final Logger logger = LoggerFactory.getLogger(Si570.class);

    public Si570(int address) {
        super(address);
    }
}