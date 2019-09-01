package org.sq5nry.plaszczka.backend.api.synthesiser;

import org.sq5nry.plaszczka.backend.impl.Reinitializable;

import java.io.IOException;

public interface FrequencyOscillator extends Reinitializable {
    /**
     * Generate frequency
     * @param freq Hz
     * @throws IOException
     */
    void setFrequency(int freq) throws IOException;
}