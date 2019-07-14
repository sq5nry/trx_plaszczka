package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import com.pi4j.io.i2c.I2CBus;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

public abstract class GenericDac extends GenericChip {
    public GenericDac(I2CBus i2CBus, int address) {
        super(i2CBus, address);
    }

    /**
     * Get DAC capacity.
     * @return
     */
    public abstract int getMaxData();

    /**
     * Reference voltage.
     * @return VRef in Volts
     */
    public abstract float getVref();

    /**
     * Set raw DAC data.
     * @param data
     * @param channel
     * @throws Exception
     */
    public abstract void setData(int data, int channel) throws Exception;

    /**
     * Set raw DAC data.
     * @param data
     * @throws Exception
     */
    public abstract void setData(int data) throws Exception;

    /**
     * Set data for output voltage.
     * @param voltage
     * @throws Exception
     */
    public void setVoltage(float voltage) throws Exception {
        setData((int) (getMaxData() * voltage / getVref()));
    }
}
