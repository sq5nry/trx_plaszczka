package org.sq5nry.plaszczka.backend.api.selectivity;

public enum Bandwidth {
    DISABLED(0), CW_500Hz(500), SSB_1k8(1800), SSB_2k4(2400), ALL_BAND(Integer.MAX_VALUE);

    int freq;
    Bandwidth(int freq) {
        this.freq = freq;
    }

    public static Bandwidth fromBandwidth(int freq) {
        for(Bandwidth bw: Bandwidth.values()) {
            if (bw.freq == freq) {
                return bw;
            }
        }
        throw new IllegalArgumentException("unsupported bandwidth frequency: " + freq + "Hz");
    }

    public String toString() {
        return freq + "Hz";
    }
}
