package org.sq5nry.plaszczka.backend.api.selectivity;

public interface Selectivity {
    String ROOT = "/selectivity";
    String RESOURCE_PATH_BANDWIDTH = ROOT + "/bw/{bw}";
    String RESOURCE_PATH_BYPASS = ROOT + "/bypass";

    void setFilter(Bandwidth bw) throws Exception;
    void bypass() throws Exception;
}
