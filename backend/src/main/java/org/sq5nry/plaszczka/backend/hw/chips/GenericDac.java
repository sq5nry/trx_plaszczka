package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2CChip;

import java.io.IOException;

public abstract class GenericDac extends GenericI2CChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericDac.class);

    public GenericDac(int address, String name) {
        super(address, name);
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
    public abstract void setData(int data, int channel) throws IOException;

    /**
     * Set raw DAC data.
     * @param data
     * @throws Exception
     */
    public abstract void setData(int data) throws IOException;

    /**
     * Set data for output voltage.
     * @param voltage
     * @throws Exception
     */
    public void setVoltage(float voltage, int channel) throws IOException {
        logger.debug("setVoltage: requested {}V, channel={}", voltage, channel);
        int dacData = calculateDacData(voltage);
        logger.debug("setVoltage: dacData={}", dacData);
        setData(dacData, channel);
    }

    /**
     * Set data for output voltage.
     * @param voltage
     * @throws Exception
     */
    public void setVoltage(float voltage) throws IOException {
        setData(calculateDacData(voltage));
    }

    private int calculateDacData(float voltage) {
        return (int) Math.ceil(getMaxData() * voltage / getVref());
    }
}
