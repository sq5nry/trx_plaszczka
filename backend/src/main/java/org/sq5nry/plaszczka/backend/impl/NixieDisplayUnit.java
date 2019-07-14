package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.hw.i2c.GenericChip;
import org.sq5nry.plaszczka.backend.hw.i2c.I2CBusProvider;
import org.sq5nry.plaszczka.backend.hw.i2c.chips.Mcp23017;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Nixie seven-tube frequency display in format XX.XXX.XX
 * Capacity up to 99999999Hz, single Hz digit not displayed.
 * Optional dot marker above a digit; single dot at a time.
 */
@Component
public class NixieDisplayUnit implements FrequencyDisplay, Reinitializable {
    private static final Logger logger = LoggerFactory.getLogger(NixieDisplayUnit.class);
    private final I2CBus bus;

    private Map<Integer, GenericChip> chipset = new HashMap<>();

    private final int EXPANDER_A_I2CADDR = 0x21;
    private final int EXPANDER_B_I2CADDR = 0x20;

    private static final boolean MARKER_ONLY = true;
    private static final boolean ALL = false;

    private int frequency;
    private byte markerPosition;
    private boolean blankLeadingZeroes = true;

    private byte[] _digits = new byte[6];
    private byte[] _port = new byte[4];

    @Autowired
    public NixieDisplayUnit(I2CBusProvider i2cBusProv) throws Exception {
        logger.debug("creating expanders");
        bus = i2cBusProv.getBus();
        initialize();
        logger.debug("expanders created & initialized");
    }

    @Override
    public void initialize() throws Exception {
        chipset.put(EXPANDER_A_I2CADDR, create(bus, EXPANDER_A_I2CADDR));
        chipset.put(EXPANDER_B_I2CADDR, create(bus, EXPANDER_B_I2CADDR));
    }

    private static Mcp23017 create(I2CBus bus, int address) throws IOException {
        logger.debug("creating expander @x{}", Integer.toHexString(address));
        Mcp23017 expander = new Mcp23017(bus, address);
        logger.debug("initializing expander @x{}", Integer.toHexString(address));
        expander.initialize();
        logger.debug("expander @x{} initialized", Integer.toHexString(address));
        expander.getDevice().write(Mcp23017.IODIR_A, Mcp23017.IODIR_ALL_OUTPUTS);
        expander.getDevice().write(Mcp23017.IODIR_B, Mcp23017.IODIR_ALL_OUTPUTS);
        logger.debug("expander @x{} configured", Integer.toHexString(address));
        return expander;
    }

    @Override
    public void setFrequency(int frequency) throws IOException {
        logger.debug("setting frequency to {}Hz", frequency);
        this.frequency = frequency;
        update(ALL);
    }

    @Override
    public void setMarker(int markerPosition) throws IOException {
        logger.debug("setting marker at position {}", markerPosition);
        this.markerPosition = (byte) (markerPosition & 0x0F);
        update(MARKER_ONLY);
    }

    @Override
    public void setBlankLeadingZeroes(boolean blankLeadingZeroes) {
        logger.debug("setting blank leading zeroes mode to {}", blankLeadingZeroes);
        this.blankLeadingZeroes = blankLeadingZeroes;
    }

    public boolean isBlankLeadingZeroes() {
        return blankLeadingZeroes;
    }

    /**
     * Get last set frequency. Actual displayed value is not verified.
     * @return frequency in Hz
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Get last set dot marker position. Actual marked position is not verified.
     * @return marker position 1..7
     *  0 if inactive
     */
    public int getMarker() {
        return markerPosition;
    }

    private void update(boolean markerOnly) throws IOException {
        logger.debug("updating display");
        byte[] p = new byte[4];
        byte d1 = (byte) ((frequency / 10) % 10);

        if (!markerOnly) {
            _digits[5] = (byte) ((frequency / 10000000) % 10);
            _digits[4] = (byte) ((frequency / 1000000) % 10);
            _digits[3] = (byte) ((frequency / 100000) % 10);
            _digits[2] = (byte) ((frequency / 10000) % 10);
            _digits[1] = (byte) ((frequency / 1000) % 10);
            _digits[0] = (byte) ((frequency / 100) % 10);

            if (blankLeadingZeroes) {
                for (int ct = 5; ct >= 0; ct--) {
                    if (_digits[ct] == 0x0) {
                        _digits[ct] = 0xA;
                    } else break;
                }
            }

            p[0] = (byte) (((_digits[5] & 0xf) << 4) | (_digits[4] & 0xf));
            p[1] = (byte) (((_digits[3] & 0xf) << 4) | (_digits[2] & 0xf));
            p[2] = (byte) (((_digits[1] & 0xf) << 4) | (_digits[0] & 0xf));

            if (p[0] != _port[0]) {   //TODO optimize
                ((Mcp23017) chipset.get(EXPANDER_A_I2CADDR)).writePort(Mcp23017.Port.GPIO_A, p[0]);
                _port[0] = p[0];
            }
            if (p[1] != _port[1]) {
                ((Mcp23017) chipset.get(EXPANDER_A_I2CADDR)).writePort(Mcp23017.Port.GPIO_B, p[1]);
                _port[1] = p[1];
            }
            if (p[2] != _port[2]) {
                ((Mcp23017) chipset.get(EXPANDER_B_I2CADDR)).writePort(Mcp23017.Port.GPIO_A, p[2]);
                _port[2] = p[2];
            }
        }

        p[3] = (byte) ((d1 << 4) | markerPosition);
        if (p[3] != _port[3]) {
            ((Mcp23017) chipset.get(EXPANDER_B_I2CADDR)).writePort(Mcp23017.Port.GPIO_B, p[3]);
            _port[3] = p[3];
        }
    }

    private boolean ticker = false;
    public void setArbitraryDigits(byte[] _digits) throws IOException {
        byte[] p = new byte[4];
        p[0] = (byte) (((_digits[0] & 0xf) << 4) | (_digits[1] & 0xf));
        p[1] = (byte) (((_digits[2] & 0xf) << 4) | (_digits[3] & 0xf));
        p[2] = (byte) (((_digits[4] & 0xf) << 4) | (_digits[5] & 0xf));
        p[3] = (byte) (((_digits[6] & 0xf) << 4) | ((ticker = !ticker) ? 5 : 0));

        ((Mcp23017) chipset.get(EXPANDER_A_I2CADDR)).writePort(Mcp23017.Port.GPIO_A, p[0]);
        ((Mcp23017) chipset.get(EXPANDER_A_I2CADDR)).writePort(Mcp23017.Port.GPIO_B, p[1]);
        ((Mcp23017) chipset.get(EXPANDER_B_I2CADDR)).writePort(Mcp23017.Port.GPIO_A, p[2]);
        ((Mcp23017) chipset.get(EXPANDER_B_I2CADDR)).writePort(Mcp23017.Port.GPIO_B, p[3]);
    }

    //TODO put into heheszki
    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() throws IOException {
        Calendar cal = Calendar.getInstance();
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        setArbitraryDigits(new byte[]{(byte) (hr/10), (byte) (hr&10), (byte) (min/10), (byte) (min%10), 0xa, (byte) (sec/10), (byte) (sec%10)});
    }
}
