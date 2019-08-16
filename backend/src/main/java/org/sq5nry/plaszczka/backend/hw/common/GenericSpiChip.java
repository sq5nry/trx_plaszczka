package org.sq5nry.plaszczka.backend.hw.common;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import static com.pi4j.wiringpi.Spi.wiringPiSPIDataRW;

@PropertySources({
        @PropertySource("classpath:io.properties")
})
public abstract class GenericSpiChip extends GenericChip {
    private static final Logger logger = LoggerFactory.getLogger(GenericSpiChip.class);

    @Value("${spi.real}")
    private boolean isSpiReal;

    public GenericSpiChip(int address) {
        super(address);
        logger.debug("isSpiReal: {}", isSpiReal);
    }

    protected int writeSpi(byte[] data, int length) {
        if (isSpiReal) {
            return wiringPiSPIDataRW(getAddress(), data, length);
        } else {
            logger.debug("dummy SPI received write request: {}", HexUtils.toHexString(data));
            return length;
        }
    }
}
