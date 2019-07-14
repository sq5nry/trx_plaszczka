package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

public abstract class GenericDac extends GenericChip implements Dac {
    public GenericDac(I2CBus i2CBus, int address) {
        super(i2CBus, address);
    }

    @Override
    public void setVoltage(float voltage) throws Exception {
        setData((int) (getMaxData() * voltage / getVref()));
    }
}
