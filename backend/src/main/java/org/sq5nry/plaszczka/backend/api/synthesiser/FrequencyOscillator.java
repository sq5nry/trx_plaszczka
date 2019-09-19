package org.sq5nry.plaszczka.backend.api.synthesiser;

import java.io.IOException;

public interface FrequencyOscillator {
    /**
     * Generate frequency
     * @param freq Hz
     * @throws IOException
     */
    void setFrequency(int freq) throws IOException;
}
