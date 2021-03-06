package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.hw.chips.Pcf8574;
import org.sq5nry.plaszczka.backend.hw.chips.Pcf8575;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.impl.common.BaseUnit;

import java.io.IOException;
import java.util.List;

@Component
public class QSDUnit extends BaseUnit implements Detector {
    private static final Logger logger = LoggerFactory.getLogger(QSDUnit.class);

    private final int EXPANDER_ADDR = 0x26;

    private Mode mode;
    private boolean qsdEnabled;
    private static final byte QSD_DISABLED_BIT = 0x02;

    private enum FeatureBits {
        CW(Mode.CW, (byte)0x01), SSB(Mode.SSB, (byte)0x00);

        Mode relatedMode;
        byte p;

        FeatureBits(Mode relatedMode, byte p) {
            this.relatedMode = relatedMode;
            this.p = p;
        }

        public byte getP() {
            return p;
        }

        public static FeatureBits getByMode(Mode mode) {
            for(FeatureBits featBits: FeatureBits.values()) {
                if (featBits.relatedMode == mode) {
                    return featBits;
                }
            }
            throw new IllegalArgumentException("unknown mode: " + mode);
        }
    }

    @Autowired
    public QSDUnit(I2CBusProvider i2cBusProv) throws Exception {
        super(i2cBusProv);
    }

    @Override
    public void createChipset(List<GenericChip> chipset) {
        chipset.add(new Pcf8575(EXPANDER_ADDR));
    }

    @Override
    public void initializeUnit() throws IOException {
        setRoofingFilter(Mode.SSB);
        setEnabled(false);
        update();
    }

    @Override
    public void setRoofingFilter(Mode mode) throws IOException {
        logger.info("setting input roofing filter for {}", mode);
        this.mode = mode;
        update();
    }

    public Mode getRoofingFilter() {
        return mode;
    }

    @Override
    public void setEnabled(boolean enabled) throws IOException {
        logger.info("detector enabled={}", enabled);
        qsdEnabled = enabled;
        update();
    }

    public boolean isEnabled() {
        return qsdEnabled;
    }

    private void update() throws IOException {
        Pcf8574 expander = (Pcf8574) getChip(EXPANDER_ADDR);
        byte qsdEn = (!qsdEnabled) ? QSD_DISABLED_BIT : 0x0;
        expander.writePort(FeatureBits.getByMode(mode).getP() | qsdEn);
    }

    @Override
    public String getName() {
        return "Noise Filters & Quadrature Sampling Detector";
    }
}
