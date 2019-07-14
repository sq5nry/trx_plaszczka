package org.sq5nry.plaszczka.backend.hw.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.impl.I2CProviderImpl;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Dummy I2C factory for local testing. Returns garbage & logs writes.
 */
public class I2CFactoryDummyBusProvider extends I2CProviderImpl {
    private static final Logger logger = LoggerFactory.getLogger(I2CFactoryDummyBusProvider.class);
    private static final Random rand = new Random();

    @Override
    protected String getFilenameForBusnumber(int busNumber) {
        return "/dev/null";
    }

    public I2CBus getBus(final int busNumber, final long lockAquireTimeout, final TimeUnit lockAquireTimeoutUnit) {
        logger.debug("returning dummy i2c hw implementation");
        return new I2CBus() {
            @Override
            public I2CDevice getDevice(int address) {
                logger.debug("returning device @{}", String.format("%02X", address));
                return new I2CDevice() {
                    private int address;

                    public I2CDevice setAddress(int address) {
                        this.address = address;
                        return this;
                    }

                    @Override
                    public int getAddress() {
                        return address;
                    }

                    @Override
                    public void write(byte b) {
                        logger.debug("[@{}] write byte x{}", String.format("%02X", address), String.format("%02X", b));
                    }

                    @Override
                    public void write(byte[] buffer, int offset, int size) {
                        logger.debug("[@{}] write part of buffer {}", String.format("%02X", address), new String(buffer));
                    }

                    @Override
                    public void write(byte[] buffer) {
                        logger.debug("[@{}] write buffer {}", String.format("%02X", address), HexUtils.toHexString(buffer));
                    }

                    @Override
                    public void write(int address, byte b) {
                        logger.debug("[@{}] write byte @x{} x{}", String.format("%02X", address), Integer.toHexString(address), String.format("%02X", b));
                    }

                    @Override
                    public void write(int address, byte[] buffer, int offset, int size) {
                        logger.debug("[@{}] write part of buffer @x{} x{}", String.format("%02X", address), Integer.toHexString(address), HexUtils.toHexString(buffer));
                    }

                    @Override
                    public void write(int address, byte[] buffer) {
                        logger.debug("[@{}] write buffer @x{} x{}", String.format("%02X", address), Integer.toHexString(address), HexUtils.toHexString(buffer));
                    }

                    @Override
                    public int read() {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(byte[] buffer, int offset, int size) {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(int address) {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(int address, byte[] buffer, int offset, int size) {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize) {
                        return rand.nextInt();
                    }
                }.setAddress(address);
            }

            @Override
            public int getBusNumber() {
                return 0;
            }

            @Override
            public void close() {
                logger.debug("closed");
            }
        };
    }
}
