package org.sq5nry.plaszczka.backend.impl.common;

import java.io.IOException;

public interface FrequencyOscillator {
    /**
     * Set generator's frequency
     * @param freq Hz
     * @throws IOException
     */
    void setFrequency(int freq) throws IOException;
}
