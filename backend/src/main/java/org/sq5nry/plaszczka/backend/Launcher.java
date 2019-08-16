package org.sq5nry.plaszczka.backend;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CProviderImpl;
import com.pi4j.wiringpi.Spi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.sq5nry.plaszczka.backend.hw.common.ChipInitializationException;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
@PropertySources({
        @PropertySource("classpath:io.properties")
})
public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        logger.info("starting application...");
        SpringApplication.run(Launcher.class, args);
    }

    @Value("${i2c.provider.class}")
    private String i2cProviderClass;

    @Value("${spi.simulated}")
    private boolean isSpiSimulated;

    @Value("${spi.channel.number}")
    private int spiChannel;

    @Value("${spi.speed}")
    private int spiSpeed;

    @Value("${gpio.provider.class}")
    private String gpioProviderClass;

    @PostConstruct
    private void init() throws Exception {
        logger.info("initializing I/O subsystems...");
        initI2c();
        initSpi();
        logger.info("I/O subsystems initialized");
    }

    private void initSpi() throws ChipInitializationException {
        logger.info("initializing wiringPiSPISetup, channel={}", spiChannel);
        if (isSpiSimulated) {
            logger.info("initializing dummy SPI, no operation");
        } else {
            int fdSpi = Spi.wiringPiSPISetup(spiChannel, spiSpeed);
            if (fdSpi <= -1) {
                logger.error("SPI bus setup failed for channel {}, FD={}", spiChannel, fdSpi);
                throw new ChipInitializationException("SPI bus setup failed for channel " + spiChannel + ", fd=" + fdSpi);
            } else {
                logger.debug("SPI initialized for channel {}, FD={}", spiChannel, fdSpi);
            }
        }
    }

    private void initI2c() throws Exception {
        logger.info("initializing I2C");
        logger.debug("setting i2c factory: {}", i2cProviderClass);
        Class clazz = Class.forName(i2cProviderClass);
        I2CProviderImpl provider = (I2CProviderImpl) clazz.newInstance();
        I2CFactory.setFactory(provider);
    }
}
