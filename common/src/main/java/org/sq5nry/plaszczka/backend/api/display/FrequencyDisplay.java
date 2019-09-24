package org.sq5nry.plaszczka.backend.api.display;

import java.io.IOException;

public interface FrequencyDisplay {
    String ROOT = "/frequencyDisplay";
    String RESOURCE_PATH_SET_FREQUENCY = ROOT + "/freq/{freq}";
    String RESOURCE_PATH_SET_MARKER= ROOT + "/marker/{position}";
    String RESOURCE_PATH_SET_BLANK_LEADING_ZEROES = ROOT + "/blanking/{enabled}";
    String RESOURCE_PATH_INITIALIZE = ROOT + "/initialize";

    /**
     * Display frequency
     * @param freq Hz 0..99999999, truncated to tens
     * @throws IOException
     */
    void setFrequency(int freq) throws IOException;

    /**
     * Shows a dot above frequency digit.
     * @param position 0..7
     *  0 - marker disabled
     *  1..7 - marker on above 1..7th digit
     * @throws IOException
     */
    void setMarker(int position) throws IOException;

    /**
     * Do not display leading zeroes. Implementation should usually enable it by default.
     * @param blankLeadingZeroes
     */
    void setBlankLeadingZeroes(boolean blankLeadingZeroes) throws IOException;
}
