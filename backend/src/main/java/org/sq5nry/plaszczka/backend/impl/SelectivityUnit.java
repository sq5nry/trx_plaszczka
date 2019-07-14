package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.selectivity.Bandwidth;
import org.sq5nry.plaszczka.backend.api.selectivity.Selectivity;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Pcf8574;

import java.util.HashMap;
import java.util.Map;

@Component
public class SelectivityUnit implements Selectivity, Reinitializable {
    private static final Logger logger = LoggerFactory.getLogger(SelectivityUnit.class);

    private final I2CBus bus;
    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int EXPANDER_ADDR = 0x23;

    private Bandwidth bw;

    private enum FeatureBits {
        BW_500(Bandwidth.CW_500Hz, (byte)0x80), BW_1K8(Bandwidth.SSB_1k8, (byte)0x10), BW_2K4(Bandwidth.SSB_2k4, (byte)0x20),
        BW_NONE(null, (byte)0x00);

        Bandwidth relatedBw;
        byte p;

        FeatureBits(Bandwidth relatedBw, byte p) {
            this.relatedBw = relatedBw;
            this.p = p;
        }

        public byte getP() {
            return p;
        }

        public static FeatureBits getByBandwidth(Bandwidth bw) {
            for(FeatureBits featBits: FeatureBits.values()) {
                if (featBits.relatedBw == bw) {
                    return featBits;
                }
            }
            throw new IllegalArgumentException("Unknown bandwidth: " + bw);
        }
    }

    @Autowired
    public SelectivityUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating chipset");
        bus = i2cBusProv.getBus();
        Pcf8574 expander = new Pcf8574(bus, EXPANDER_ADDR);
        expander.initialize();
        chipset.put(EXPANDER_ADDR, expander);
        initialize();
        logger.debug("chipset created & initialized");
    }

    @Override
    public void initialize() throws Exception {
        FeatureBits defBw = FeatureBits.BW_NONE;
        logger.debug("initializing unit with defaults: {}", defBw);
        Pcf8574 expander = (Pcf8574) chipset.get(EXPANDER_ADDR);
        expander.writePort(defBw.getP());
    }

    @Override
    public void setFilter(Bandwidth bandwidth) throws Exception {
        logger.debug("setting {} selectivity filter", bandwidth);
        this.bw = bandwidth;
        FeatureBits bw = FeatureBits.getByBandwidth(bandwidth);
        logger.debug("setting bandwidth filter: {}", bw);
        Pcf8574 expander = (Pcf8574) chipset.get(EXPANDER_ADDR);
        expander.writePort(bw.getP());
    }

    public Bandwidth getFilter() {
        return bw;
    }
}