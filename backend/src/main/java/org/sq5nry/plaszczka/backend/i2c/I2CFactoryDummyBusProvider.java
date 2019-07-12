package org.sq5nry.plaszczka.backend.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class I2CFactoryDummyBusProvider extends I2CProviderImpl {
    private static final Logger logger = LoggerFactory.getLogger(I2CFactoryDummyBusProvider.class);
    private static final Random rand = new Random();

    @Override
    protected String getFilenameForBusnumber(int busNumber) throws I2CFactory.UnsupportedBusNumberException {
        return "/dev/null";
    }

    public I2CBus getBus(final int busNumber, final long lockAquireTimeout, final TimeUnit lockAquireTimeoutUnit) throws I2CFactory.UnsupportedBusNumberException, IOException {
        logger.debug("returning dummy i2c bus implementation");
        return new I2CBus() {
            @Override
            public I2CDevice getDevice(int address) throws IOException {
                return new I2CDevice() {
                    @Override
                    public int getAddress() {
                        return 0;
                    }

                    @Override
                    public void write(byte b) throws IOException {
                        logger.debug("write byte {}", b);
                    }

                    @Override
                    public void write(byte[] buffer, int offset, int size) throws IOException {
                        logger.debug("write part of buffer {}", new String(buffer));
                    }

                    @Override
                    public void write(byte[] buffer) throws IOException {
                        logger.debug("write buffer {}", new String(buffer));
                    }

                    @Override
                    public void write(int address, byte b) throws IOException {
                        logger.debug("write byte at address {} {}", b, address);
                    }

                    @Override
                    public void write(int address, byte[] buffer, int offset, int size) throws IOException {
                        logger.debug("write part of buffer at address {} {}", new String(buffer), address);
                    }

                    @Override
                    public void write(int address, byte[] buffer) throws IOException {
                        logger.debug("write buffer at address {} {}", new String(buffer), address);
                    }

                    @Override
                    public int read() throws IOException {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(byte[] buffer, int offset, int size) throws IOException {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(int address) throws IOException {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(int address, byte[] buffer, int offset, int size) throws IOException {
                        return rand.nextInt();
                    }

                    @Override
                    public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize) throws IOException {
                        return rand.nextInt();
                    }
                };
            }

            @Override
            public int getBusNumber() {
                return 0;
            }

            @Override
            public void close() throws IOException {
                logger.debug("closed");
            }
        };
    }
}
