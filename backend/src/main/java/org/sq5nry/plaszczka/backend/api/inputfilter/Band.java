package org.sq5nry.plaszczka.backend.api.inputfilter;

public enum Band {
    M4("4m", "70MHz"), M6("6m", "50MHz"), M10("10m", "28MHz"), M12("12m", "24MHz"),
    M15("15m", "21MHz"), M17("17m", "18MHz"), M20("20m", "14MHz"), M30("30m", "10MHz"),
    M40("40m", "7MHz"), M60("60m", "5MHz"), M80("80m", "3.5MHz"), M160("160m", "1.8MHz");

    String meters, mhz;
    Band(String meters, String mhz) {
        this.meters = meters;
        this.mhz = mhz;
    }

    public static Band fromMeters(String meters) {
        for(Band band: Band.values()) {
            if (band.meters.equals(meters)) {
                return band;
            }
        }
        throw new IllegalArgumentException("Unknown band: " + meters);
    }

    public String getMeters() {
        return meters;
    }

    public String getMHz() {
        return mhz;
    }

    @Override
    public String toString() {
        return "Band{" + meters + ", " + mhz + '}';
    }
}
