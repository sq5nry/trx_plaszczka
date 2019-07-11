package org.sq5nry.plaszczka.backend;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CFactoryProviderBananaPi;
import org.sq5nry.plaszczka.backend.impl.NixieDisplay;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CFactory.setFactory(new I2CFactoryProviderBananaPi());    //TODO config
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_2);          //TODO config

        NixieDisplay display = new NixieDisplay(bus);
        display.setFrequency(1402634);
    }
}
