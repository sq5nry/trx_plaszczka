package org.sq5nry.plaszczka.backend.api.mixer;

import org.sq5nry.plaszczka.backend.api.Mode;

public interface HModeMixer {
    String ROOT = "/mixer";
    String RESOURCE_PATH_ROOFING = ROOT + "/roofingMode/{mode}";
    String RESOURCE_PATH_BIAS = ROOT + "/bias/{val}";
    String RESOURCE_PATH_SQUARER = ROOT + "/squarerThreshold/{val}";

    /**
     * Adjust mixer DC bias
     * @param voltage 0..5V
     */
    void setBiasPoint(float voltage) throws Exception;

    /**
     * Adjust LO squarer threshold
     * @param percentage 0..100
     */
    void setSquarerThreshold(float percentage) throws Exception;

    /**
     * Post mixer roofing filter.
     * @param mode
     */
    void setRoofingFilter(Mode mode) throws Exception;;
}
