package org.sq5nry.plaszczka.backend.hw.i2c;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class I2CConfiguration {
    @Value("${i2c.provider.class}")
    private String i2cProviderClass;

    @Value("${i2c.bus.number}")
    private int busNr;


    public String getI2cProviderClass() {
        return i2cProviderClass;
    }

    public int getBusNr() {
        return busNr;
    }
}
