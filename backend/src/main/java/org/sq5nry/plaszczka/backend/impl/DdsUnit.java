package org.sq5nry.plaszczka.backend.impl;

import org.sq5nry.plaszczka.backend.api.synthesiser.Dds;
import org.sq5nry.plaszczka.backend.hw.chips.Ad9954;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;

import java.io.IOException;
import java.util.List;

//@Component
public class DdsUnit extends Unit implements Dds {
    private Ad9954 dds;

    //TODO nie chce i2c
    public DdsUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
        dds = new Ad9954(500000000);
        dds.initialize();
    }

    @Override
    public void setFrequency(int freq) throws IOException {
        dds.setFrequency(freq);
    }

    @Override
    public void configure() throws IOException {

    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        //just a single hero
    }

    @Override
    public void initializeUnit() throws Exception {

    }
}
