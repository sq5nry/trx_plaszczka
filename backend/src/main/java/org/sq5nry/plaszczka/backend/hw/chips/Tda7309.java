package org.sq5nry.plaszczka.backend.hw.chips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericI2cChip;

import java.io.IOException;

/**
 * The TDA7309 is a control processor with independent left and right volume control for quality audio
 * applications. Selectable external loudness and soft mute functions are provided.
 * Control is accomplished by serial I2C bus microprocessor interface.
 */
public class Tda7309 extends GenericI2cChip {
    private static final Logger logger = LoggerFactory.getLogger(Tda7309.class);

    private static final byte CTRL_VOLUME = 0x00;
    private static final byte CTRL_MUTE_LOUD = (byte) 0x80;
    private static final byte CTRL_INPUTS = (byte) 0xa0;
    private static final byte CTRL_CHANNEL = (byte) 0xc0;

    public static final byte CHANNEL_RIGHT = 0x0;
    public static final byte CHANNEL_LEFT = 0x1;
    public static final byte CHANNEL_BOTH = 0x2;

    public static final byte INPUTS_MUTE = 0x0;
    public static final byte INPUTS_IN2 = 0x1;
    public static final byte INPUTS_IN3 = 0x2;
    public static final byte INPUTS_IN1 = 0x3;

    private static final byte MUTE_MASK = 0x3;
    public static final byte MUTE_SLOW_SOFT_MUTE_ON = 0x0;
    public static final byte MUTE_FAST_SOFT_MUTE_ON = 0x1;
    public static final byte MUTE_SOFT_MUTE_OFF= 0x2;
    private static final byte LOUD_MASK = 0xc;
    public static final byte LOUD_OFF = 0x4;
    public static final byte LOUD_ON_10DB = 0x0;
    public static final byte LOUD_ON_20DB = 0x8;

    public static final int VOLUME_MIN = 95;

    private byte volume = VOLUME_MIN;
    private byte mute = MUTE_SOFT_MUTE_OFF;
    private byte loud = LOUD_OFF;
    private byte input = INPUTS_MUTE;
    private byte channel = CHANNEL_BOTH;

    public Tda7309(int address) {
        super(address);
    }

    //TODO i2c reports error (but chip accepts ok) for any odd value written, need to replace? suppress err for now
    private void writeTmp(byte val) throws IOException {
        byte data = val; //(val & 0xfe)
        if (logger.isDebugEnabled()) {
            logger.debug("write: {}", String.format("%02X", data));
        }
        try {
            getDevice().write(data);
        } catch (IOException e) {
            //TODO workaround, ignore
        }
    }
    /**
     * Set volume.
     * @param volume 0..95dB reflecting negative actual value. Higher value corresponds to MUTE condition
     */
    public void setVolume(int volume) throws IOException {
        if (volume<0) {
            throw new IllegalArgumentException("negative volume");
        }
        this.volume = (byte) (volume & 0x7F);
        writeTmp((byte) (CTRL_VOLUME | volume));
    }

    /**
     * Set Input Multiplexer
     * @param input INPUTS_IN1, INPUTS_IN2, INPUTS_IN3, INPUTS_MUTE
     * @throws IOException
     */
    public void setInputMux(byte input) throws IOException {
        writeTmp((byte) (CTRL_INPUTS | input));
        this.input = input;
    }

    /**
     * Set Channel
     * @param channel CHANNEL_RIGHT, CHANNEL_LEFT, CHANNEL_BOTH
     * @throws IOException
     */
    public void setChannel(byte channel) throws IOException {
        writeTmp((byte) (CTRL_CHANNEL | channel));
        this.channel = channel;
    }

    /**
     * Set Mute
     * @param mute MUTE_LOUD_SLOW_SOFT_MUTE_ON, MUTE_LOUD_FAST_SOFT_MUTE_ON, MUTE_LOUD_SOFT_MUTE_OFF
     * @throws IOException
     */
    public void setMute(byte mute) throws IOException {
        this.mute = (byte) (mute & MUTE_MASK);
        setMuteLoud();
    }

    /**
     * Set Loudness
     * @param loud LOUD_ON_10DB, LOUD_ON_20DB, LOUD_OFF
     * @throws IOException
     */
    public void setLoudness(byte loud) throws IOException {
        this.loud = (byte) (loud & LOUD_MASK);
        setMuteLoud();
    }

    private void setMuteLoud() throws IOException {
        writeTmp((byte) (this.mute | this.loud | CTRL_MUTE_LOUD));
    }
}
