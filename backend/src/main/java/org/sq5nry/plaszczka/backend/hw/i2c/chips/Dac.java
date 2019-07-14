package org.sq5nry.plaszczka.backend.hw.i2c.chips;

public interface Dac {
    /**
     * Get DAC capacity.
     * @return
     */
    int getMaxData();

    /**
     * Reference voltage.
     * @return VRef in Volts
     */
    float getVref();

    /**
     * Set raw DAC data.
     * @param data
     * @throws Exception
     */
    void setData(int data) throws Exception;

    /**
     * Set data for output voltage.
     * @param voltage
     * @throws Exception
     */
    void setVoltage(float voltage) throws Exception;
}
