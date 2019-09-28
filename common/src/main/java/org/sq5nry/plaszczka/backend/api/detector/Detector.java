package org.sq5nry.plaszczka.backend.api.detector;

import org.sq5nry.plaszczka.backend.api.Mode;

import java.io.IOException;

public interface Detector {
    String ROOT = "/detector";
    String RESOURCE_PATH_ROOFING = ROOT + "/mode/{mode}";
    String RESOURCE_PATH_ENABLE = ROOT + "/enabled/{enabled}";

    /**
     * Pre-detector roofing filter.
     * @param mode
     */
    void setRoofingFilter(Mode mode) throws IOException;

    /**
     * Enable main QSD.
     * @param enabled
     */
    void setEnabled(boolean enabled) throws IOException;
}
