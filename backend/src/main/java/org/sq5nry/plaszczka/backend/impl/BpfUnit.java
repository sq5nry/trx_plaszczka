package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.inputfilter.Band;
import org.sq5nry.plaszczka.backend.api.inputfilter.BandPassFilter;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.chips.Pcf8575;

import java.io.IOException;
import java.util.List;

@Component
//@PropertySource("classpath:trx_config.properties")
public class BpfUnit extends Unit implements BandPassFilter {
    private static final Logger logger = LoggerFactory.getLogger(BpfUnit.class);

    private static final int EXPANDER_ADDR = 0x27;//TODO config

    private static final String DEFAULT_BAND = "20m"; //TODO from config doesnt work

    private Band band;
    private byte attenuation = 0;
    private byte[] buffer;

    private enum FeatureBits {
        M6(Band.M6, (byte)0x00, (byte)0x02), M10(Band.M10, (byte)0x00, (byte)0x01), M12(Band.M12, (byte)0x80, (byte)0x00),
        M15(Band.M15, (byte)0x40, (byte)0x00), M17(Band.M17, (byte)0x20, (byte)0x00), M20(Band.M20, (byte)0x10, (byte)0x00),
        M30(Band.M30, (byte)0x08, (byte)0x00), M40(Band.M40, (byte)0x04, (byte)0x00), M80(Band.M80, (byte)0x02, (byte)0x00),
        M160(Band.M160, (byte)0x01, (byte)0x00), M4(Band.M4, (byte)0x00, (byte)0x08), M60(Band.M60, (byte)0x00, (byte)0x04);

        Band relatedBand;
        byte p0;
        byte p1;

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
        super(i2cBusProv);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(new Pcf8575(EXPANDER_ADDR));
    }

    public void initializeUnit() throws Exception {
        super.initializeUnit();
        setBand(Band.fromMeters(DEFAULT_BAND));
    }

    @Override
    public void setBand(Band band) throws IOException {
        logger.info("setting band to {}", band);
        this.band = band;
        update();
    }

    @Override
    public void setAttenuation(int db) throws IOException {
        if (db < 0 || db > 30) {
            throw new IllegalArgumentException("Attenuation out of range 0..30dB");
        }
        logger.info("setting attenuation to {}dB", db);
        this.attenuation = (byte) (db >> 1);
        update();
    }

    private void update() throws IOException {
        FeatureBits bits = FeatureBits.getByBand(band);
        if (buffer == null) {
            buffer = new byte[2];
        }
        buffer[0] = bits.getP0();
        buffer[1] = (byte) (bits.getP1() | (attenuation << 4));
        ((Pcf8575) getChip(EXPANDER_ADDR)).writePort(buffer);
    }

    public Band getBand() {
        return band;
    }

    public int getAttenuation() {
        return attenuation << 1;
    }
}
