package org.sq5nry.plaszczka.backend.api.synthesiser;

import java.io.IOException;

public interface Synthesizer {
    String RESOURCE_PATH_BFO = "/bfo/{freq}";
    String RESOURCE_PATH_VFO = "/vfo/{freq}";

    /**
     * Set BFO frequency
     * @param freq Hz
     * @throws IOException
     */
    void setBfoFrequency(int freq) throws IOException;

    /**
     * Set VFO frequency
     * @param freq Hz
     * @throws IOException
     */
    void setVfoFrequency(int freq) throws IOException;
}
