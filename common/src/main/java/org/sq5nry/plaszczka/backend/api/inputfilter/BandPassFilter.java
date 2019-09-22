package org.sq5nry.plaszczka.backend.api.inputfilter;

import java.io.IOException;

public interface BandPassFilter {
    String ROOT = "/bandPassFilter";
    String RESOURCE_PATH = ROOT + "band/{band}";

    /**
     * Enables a requested input Band Pass Filter
     * @param band
     * @throws IOException
     * @return result
     */
    String setBand(Band band) throws IOException;
}
