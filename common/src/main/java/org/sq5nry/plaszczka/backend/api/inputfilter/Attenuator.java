package org.sq5nry.plaszczka.backend.api.inputfilter;

import java.io.IOException;

public interface Attenuator {
    String ROOT = "/attenuator";
    String RESOURCE_PATH = ROOT + "/{att}";

    /**
     * Enable variable attenuator before a filter
     *
     * @param db Attenuation in dB from 2 to 30[dB]
     * @return result
     */
    int setAttenuation(int db) throws IOException;
}
