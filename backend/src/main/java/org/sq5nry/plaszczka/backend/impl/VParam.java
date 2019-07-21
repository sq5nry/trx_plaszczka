package org.sq5nry.plaszczka.backend.impl;

/**
 * Unit-to-unit scaling with 2-point linear approximation.
 */
public final class VParam {
    public VParam(String name, String inUnit, float min, float max, float fullScale, float scalingFactorX, float scalingFactorY, float b, boolean proportional, float recommended) {
        this.name = name;
        this.inUnit = inUnit;
        this.min = min;
        this.max = max;
        this.fullScale = fullScale;
        this.scalingFactorX = scalingFactorX;
        this.scalingFactorY = scalingFactorY;
        this.proportional = proportional;
        this.recommended = recommended;
        this.b = b;
    }

    private String name;
    private String inUnit;
    private float min;
    private float max;
    private boolean proportional;
    private float recommended;

    private float fullScale;
    private float scalingFactorX;
    private float scalingFactorY;
    private float b;    // y=(X*FS/Y)x + b

    public float convertToVoltage(float in) {  //TODO name
        if (in < min || in > max) {
            throw new IllegalArgumentException(name + " outside range " + min + "-" + max + " [" + inUnit + "]");
        } else {
            if (proportional) {
                return b + (scalingFactorX * in / (fullScale * scalingFactorY));
            } else {
                return b + (scalingFactorX * fullScale / (in * scalingFactorY));
            }
        }
    }

    private static final float DAC_STEP = 5.0f /256; //TODO couple to GenericDac props
    public enum VP {
        Vsph(new VParam("Vsph", "ms/131dB", 84.8f, 21600f, 131f, 1000 * DAC_STEP, 6.06f, 0,false, 675)),
        Vspa(new VParam("Vspa", "ms/131dB", 84.8f, 21600f, 131f, 1000 * DAC_STEP, 6.06f, 0, false,2700)),
        Vleak(new VParam("Vleak", "dB/s", 0f, 140.7f, 1, 11*0.455f, 140.7f, 0, true, 2.21f)),
        Vfloor(new VParam("Vfloor", "dB", 0f, 25.5f, 1, 10*19.6f, 1000, 0, true, 15.1f)),   //19.6mV is 5/256 with 1000
        Vath(new VParam("Vath", "dBm", -131f, 0f, 1, 3.894f, 131f,3.894f, true, -73.3f)),
        Vhth(new VParam("Vhth", "dB", 0.515f, 131f, 1, 3.894f-0.0153f, 131f-0.515f,0.0153f-(0.515f*(3.894f-0.0153f)/(131f-0.515f)), true, 10.3f)),
        Vloop(new VParam("Vloop", "dB", 0f, 131f, 1, 3.894f, 131f,0, true, 6.2f)), //TODO 1 bit off for recommended val
        Vgain(new VParam("Vgain", "dB", -21.5f, 109.6f, 1, -3.894f, 131f,3.894f*109.6f/131f, true, 109.6f)),//TODO 256 for min!
        Vspd(new VParam("Vspd", "ms", 122f, 31000f, 1, -5f*255f/256f, 31000-122,5f+(122f*(5f*255f/256f)/(31000-122)), true, 2000f)),//TODO 1bit offs
        Attack(new VParam("Attack", "ms", 73f, 400f, 1, 100, 400-73,-73f*(100f/(400-73)), true, 85f)),//TODO 1bit offs
        ;

        VParam param;
        VP(VParam vParam) {
            this.param = vParam;
        }

        VParam getParam() {
            return param;
        }
    }
}
