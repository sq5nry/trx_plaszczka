package org.sq5nry.plaszczka.backend.api.mixer;

import org.sq5nry.plaszczka.backend.api.Mode;

public interface HModeMixer {
    /**
     * Adjust mixer DC bias
     * @param bias 0..4096
     */
    void setBiasPoint(int bias);

    /**
     * Adjust LO squarer threshold
     * @param threshold 0..4096
     */
    void setSquarerThreshold(int threshold);

    /**
     * Post mixer roofing filter.
     * @param mode
     */
    void setRoofingFilter(Mode mode);
}
