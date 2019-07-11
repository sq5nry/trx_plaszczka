package org.sq5nry.plaszczka.backend.api.display;

import java.io.IOException;

public interface FrequencyDisplay {
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
    void setBlankLeadingZeroes(boolean blankLeadingZeroes);
}
