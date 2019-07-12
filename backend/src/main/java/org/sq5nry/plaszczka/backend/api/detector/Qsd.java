package org.sq5nry.plaszczka.backend.api.detector;

import org.sq5nry.plaszczka.backend.api.Mode;

public interface Qsd {
    /**
     * Pre-detector roofing filter.
     * @param mode
     */
    void setRoofingFilter(Mode mode);

    /**
     * Enable main QSD.
     * @param enabled
     */
    void setEnabled(boolean enabled);
}
