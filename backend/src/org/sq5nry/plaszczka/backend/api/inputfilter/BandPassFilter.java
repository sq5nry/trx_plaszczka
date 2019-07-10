package org.sq5nry.plaszczka.backend.api.inputfilter;

public interface BandPassFilter {
    void setBand(Band band);

    /**
     * Enable variable attenuator before a filter
     * @param db Attenuation in dB from 2 to 30[dB]
     */
    void setAttenuation(int db);
}
