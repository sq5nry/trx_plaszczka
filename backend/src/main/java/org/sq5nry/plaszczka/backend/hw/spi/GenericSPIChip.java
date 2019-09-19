package org.sq5nry.plaszczka.backend.hw.spi;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.common.GenericChip;

import static com.pi4j.wiringpi.Spi.wiringPiSPIDataRW;

public abstract class GenericSPIChip extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericSPIChip.class);
    private boolean isSpiSimulated;

    public GenericSPIChip(SPIConfiguration spiConfig, String name) {
        super(spiConfig.getSpiChannel(),name);
        isSpiSimulated = spiConfig.isSpiSimulated();
    }

    protected int writeSpi(byte[] data, int length) {
        if (isSpiSimulated) {
            logger.debug("dummy SPI received write request: {}", HexUtils.toHexString(data));
            return length;
        } else {
            return wiringPiSPIDataRW(getAddress(), data, length);
        }
    }
}
