package org.sq5nry.plaszczka.backend.api.detector;

import org.sq5nry.plaszczka.backend.api.Mode;

public interface Detector {
    /**
     * Pre-detector roofing filter.
     * @param mode
     */
    void setRoofingFilter(Mode mode) throws Exception;

    /**
     * Enable main QSD.
     * @param enabled
     */
    void setEnabled(boolean enabled) throws Exception;
}
