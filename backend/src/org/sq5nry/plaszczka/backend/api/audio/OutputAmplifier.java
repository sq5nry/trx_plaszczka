package org.sq5nry.plaszczka.backend.api.audio;

public class OutputAmplifier {
    boolean headphones;
    boolean speaker;
    boolean rec;

    public OutputAmplifier(boolean headphones, boolean speaker, boolean rec) {
        this.headphones = headphones;
        this.speaker = speaker;
        this.rec = rec;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
    }

    public void setSpeaker(boolean speaker) {
        this.speaker = speaker;
    }

    public void setRec(boolean rec) {
        this.rec = rec;
    }
}
