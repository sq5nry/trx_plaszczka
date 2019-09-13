package org.sq5nry.plaszczka.backend.api.inputfilter;

import java.io.IOException;

public interface BandPassFilter {
    void setBand(Band band) throws IOException;

    /**
     * Enable variable attenuator before a filter
     * @param db Attenuation in dB from 2 to 30[dB]
     * @return actual set attenuation
     */
    int setAttenuation(int db) throws IOException;
}
