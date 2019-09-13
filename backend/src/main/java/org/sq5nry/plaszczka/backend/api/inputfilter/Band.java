package org.sq5nry.plaszczka.backend.api.inputfilter;

public enum Band {
    M4("4m", "70MHz", 70000000, 70000000),
    M6("6m", "50MHz", 52000000, 54000000),
    M10("10m", "28MHz", 28000000, 29700000),
    M12("12m", "24MHz", 24890000, 24980000),
    M15("15m", "21MHz", 21050000, 21450000),
    M17("17m", "18MHz", 18068000, 18168000),
    M20("20m", "14MHz", 14000000, 14350000),
    M30("30m", "10MHz", 10100000, 10150000),
    M40("40m", "7MHz", 7000000, 7200000),
    M60("60m", "5MHz", 5357000, 5362500),
    M80("80m", "3.5MHz", 3500000, 3800000),
    M160("160m", "1.8MHz", 1800000, 2000000),
    NONE("N/A", "N/A", -1, -1);

    String meters;
    String mhz;
    int startFreq;
    int endFreq;

    Band(String meters, String mhz, int startFreq, int endFreq) {
        this.meters = meters;
        this.mhz = mhz;
        this.startFreq = startFreq;
        this.endFreq = endFreq;
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

    public int getStartFreq() {
        return startFreq;
    }

    @Override
    public String toString() {
        return "Band{" + meters + ", " + mhz + '}';
    }
}