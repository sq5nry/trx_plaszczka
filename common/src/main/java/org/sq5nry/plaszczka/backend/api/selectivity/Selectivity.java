package org.sq5nry.plaszczka.backend.api.selectivity;

import java.io.IOException;

public interface Selectivity {
    String ROOT = "/selectivity";
    String RESOURCE_PATH_BANDWIDTH = ROOT + "/bw/{bw}";
    String RESOURCE_PATH_BYPASS = ROOT + "/bypass";

    void setFilter(Bandwidth bw) throws IOException;
    void bypass() throws IOException;
}
