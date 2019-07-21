package org.sq5nry.plaszczka.backend.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8574;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8575;

import java.io.IOException;

import static org.sq5nry.plaszczka.backend.impl.Unit.State.FAILED;

@Component
public class QSDUnit extends Unit implements Detector {
    private static final Logger logger = LoggerFactory.getLogger(QSDUnit.class);

    private final int EXPANDER_ADDR = 0x26;

    private Mode mode;
    private boolean qsdEnabled;
    private static final byte QSD_ENABLED_BIT = 0x02;

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
        addToChipset(new Pcf8575(EXPANDER_ADDR));
        initializeChipset();
        initializeUnit();
    }

    @Override
    public void initializeUnit() {
        super.initializeUnit();
        logger.debug("initializing unit with defaults");
        mode = Mode.SSB;
        qsdEnabled = false;
        try {
            update();
        } catch(Exception e) {
            logger.debug("unit initialization failed", e);  //TODO move to template
            state = FAILED;
        }
    }

    @Override
    public void setRoofingFilter(Mode mode) throws Exception {
        logger.debug("setting input roofing filter for {}", mode);
        this.mode = mode;
        update();
    }

    public Mode getRoofingFilter() {
        return mode;
    }

    @Override
    public void setEnabled(boolean enabled) throws Exception {
        logger.debug("detector enabled={}", enabled);
        qsdEnabled = enabled;
        update();
    }

    public boolean isEnabled() {
        return qsdEnabled;
    }

    private void update() throws IOException {
        Pcf8574 expander = (Pcf8574) getChip(EXPANDER_ADDR);
        byte qsdEn = qsdEnabled ? QSD_ENABLED_BIT : 0x0;
        expander.writePort(FeatureBits.getByMode(mode).getP() | qsdEn);
    }
}
