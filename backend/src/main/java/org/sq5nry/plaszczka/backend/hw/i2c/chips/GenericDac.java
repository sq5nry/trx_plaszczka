package org.sq5nry.plaszczka.backend.hw.i2c.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;

public abstract class GenericDac extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericDac.class);

    public GenericDac(int address) {
        super(address);
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
    public void setVoltage(float voltage, int channel) throws Exception {
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
    public void setVoltage(float voltage) throws Exception {
        setData(calculateDacData(voltage));
    }

    private int calculateDacData(float voltage) {
        return (int) Math.ceil(getMaxData() * voltage / getVref());
    }
}
