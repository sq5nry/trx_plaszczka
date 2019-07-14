package org.sq5nry.plaszczka.backend.api.audio;

public class OutputAmplifier {
    public static final String HEADPHONES = "head";
    public static final String SPEAKERS = "speaker";
    public static final String REC = "rec";

    private boolean headphones = false;
    private boolean speaker = false;
    private boolean rec = false;

    public OutputAmplifier(boolean headphones, boolean speaker, boolean rec) {
        this.headphones = headphones;
        this.speaker = speaker;
        this.rec = rec;
    }

    public OutputAmplifier(String output) {
        headphones = output.contains(HEADPHONES);
        speaker = output.contains(SPEAKERS);
        rec = output.contains(REC);
    }

    public boolean isHeadphones() {
        return headphones;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
    }

    public boolean isSpeaker() {
        return speaker;
    }

    public void setSpeaker(boolean speaker) {
        this.speaker = speaker;
    }

    public boolean isRec() {
        return rec;
    }

    public void setRec(boolean rec) {
        this.rec = rec;
    }

    @Override
    public String toString() {
        return "OutputAmplifier{" +
                "headphones=" + headphones +
                ", speaker=" + speaker +
                ", rec=" + rec +
                '}';
    }
}
