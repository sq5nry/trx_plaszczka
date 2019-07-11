package org.sq5nry.plaszczka.backend.impl;

import com.pi4j.io.i2c.I2CBus;
import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.i2c.chips.Mcp23017;

import java.io.IOException;

/**
 * Nixie seven-tube frequency display in format XX.XXX.XX
 * Capacity up to 99999999Hz, single Hz digit not displayed.
 * Optional dot marker above a digit; single dot at a time.
 */
public class NixieDisplay implements FrequencyDisplay {
    /* chipset */
    private Mcp23017 expanderA;
    private Mcp23017 expanderB;
    private final int EXPANDER_A_I2CADDR = 0x21;
    private final int EXPANDER_B_I2CADDR = 0x20;

    private static final boolean MARKER_ONLY = true;
    private static final boolean ALL = false;

    private int frequency;
    private byte markerPosition;
    private boolean blankLeadingZeroes = true;

    private byte[] _digits = new byte[6];

    public NixieDisplay(I2CBus bus) throws IOException {
        expanderA = create(bus, EXPANDER_A_I2CADDR);
        expanderB = create(bus, EXPANDER_B_I2CADDR);
    }

    private static Mcp23017 create(I2CBus bus, int address) throws IOException {
        Mcp23017 expander = new Mcp23017(bus, address);
        expander.initialize();
        expander.getDevice().write(Mcp23017.IODIR_A, Mcp23017.IODIR_ALL_OUTPUTS);
        expander.getDevice().write(Mcp23017.IODIR_B, Mcp23017.IODIR_ALL_OUTPUTS);
        return expander;
    }

    @Override
    public void setFrequency(int frequency) throws IOException {
        this.frequency = frequency;
        update(ALL);
    }

    @Override
    public void setMarker(int markerPosition) throws IOException {
        this.markerPosition = (byte) (markerPosition & 0x0F);
        update(MARKER_ONLY);
    }

    @Override
    public void setBlankLeadingZeroes(boolean blankLeadingZeroes) {
        this.blankLeadingZeroes = blankLeadingZeroes;
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

            byte p1 = (byte) (((_digits[5] & 0xf) << 4) | (_digits[4] & 0xf));
            byte p2 = (byte) (((_digits[3] & 0xf) << 4) | (_digits[2] & 0xf));
            byte p3 = (byte) (((_digits[1] & 0xf) << 4) | (_digits[0] & 0xf));

            expanderA.writePort(Mcp23017.Port.GPIO_A, p1);
            expanderA.writePort(Mcp23017.Port.GPIO_B, p2);
            expanderB.writePort(Mcp23017.Port.GPIO_A, p3);
        }

        byte p4 = (byte) ((d1 << 4) | markerPosition);
        expanderB.writePort(Mcp23017.Port.GPIO_B, p4);
    }
}
