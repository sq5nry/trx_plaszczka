package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.apache.commons.lang3.BitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.inputfilter.Band;
import org.sq5nry.plaszczka.backend.api.inputfilter.BandPassFilter;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8575;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class BpfUnit implements BandPassFilter {
    private static final Logger logger = LoggerFactory.getLogger(BpfUnit.class);

    private I2CBus bus;

    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int EXPANDER_I2CADDR = 0x27;//TODO config

    private Band band = Band.M20;   //TODO config
    private byte attenuation = 0;    //TODO config
    private byte[] buffer = new byte[2];

    private BitField ATT = new BitField(0xF000);

    private enum FeatureBits {
        M6(Band.M6, (byte)0x00, (byte)0x02), M10(Band.M10, (byte)0x00, (byte)0x01), M12(Band.M12, (byte)0x80, (byte)0x00),
        M15(Band.M15, (byte)0x40, (byte)0x00), M17(Band.M17, (byte)0x20, (byte)0x00), M20(Band.M20, (byte)0x10, (byte)0x00),
        M30(Band.M30, (byte)0x08, (byte)0x00), M40(Band.M40, (byte)0x04, (byte)0x00), M80(Band.M80, (byte)0x02, (byte)0x00),
        M160(Band.M160, (byte)0x01, (byte)0x00), M4(Band.M4, (byte)0x00, (byte)0x08), M60(Band.M60, (byte)0x00, (byte)0x04);

        Band relatedBand;
        byte p0, p1;

        FeatureBits(Band relatedBand, byte p0, byte p1) {
            this.relatedBand = relatedBand;
            this.p0 = p0;
            this.p1 = p1;
        }

        public byte getP0() {
            return p0;
        }

        public byte getP1() {
            return p1;
        }

        public static FeatureBits getByBand(Band band) {
            for(FeatureBits featBits: FeatureBits.values()) {
                if (featBits.relatedBand == band) {
                    return featBits;
                }
            }
            throw new IllegalArgumentException("Unknown band: " + band);
        }
    }

    @Autowired
    public BpfUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating expanders");
        bus = i2cBusProv.getBus();
        chipset.put(EXPANDER_I2CADDR, create(bus, EXPANDER_I2CADDR));
        logger.debug("expander created & initialized");
    }

    private static Pcf8575 create(I2CBus bus, int address) throws IOException {
        logger.debug("creating expander @x{}", Integer.toHexString(address));
        Pcf8575 expander = new Pcf8575(bus, address);
        logger.debug("initializing expander @x{}", Integer.toHexString(address));
        expander.initialize();
        logger.debug("expander @x{} initialized", Integer.toHexString(address));
        return expander;
    }

    @Override
    public void setBand(Band band) throws IOException {
        logger.debug("setting band to {}", band);
        this.band = band;
        update();
    }

    @Override
    public void setAttenuation(int db) throws IOException {
        if (db < 0 || db > 32) {
            throw new IllegalArgumentException("Attenuation out of range 0..32dB");
        }
        logger.debug("setting attenuation to {}dB", db);
        this.attenuation = (byte) (db & 0x0F);
        update();
    }

    private void update() throws IOException {
        FeatureBits bits = FeatureBits.getByBand(band);
        buffer[0] = bits.getP0();
        buffer[1] = (byte) (bits.getP1() | ATT.setByte(attenuation));
        ((Pcf8575) chipset.get(EXPANDER_I2CADDR)).writePort(buffer);
    }

    public Band getBand() {
        return band;
    }

    public byte getAttenuation() {
        return attenuation;
    }
}
