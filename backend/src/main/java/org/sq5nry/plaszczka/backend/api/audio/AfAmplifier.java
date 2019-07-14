package org.sq5nry.plaszczka.backend.api.audio;

public interface AfAmplifier {
    /**
     * Set input channel routing from IQ input to stereo amplifier.
     * @param mode
     */
    void setInput(InputSelector mode) throws Exception;

    /**
     * Set volume.
     * @param volume -dB, 0..-95dB
     */
    void setVolume(int volume, Channel channel) throws Exception;

    /**
     * Set loud or mute effects.
     * @param loudness
     */
    void setMuteLoudness(MuteAndLoudness loudness) throws Exception;

    /**
     * Set output amplifier(s).
     * @param amp which output amplifier(s) is(are) enabled
     */
    void setOutputAmplifier(OutputAmplifier amp) throws Exception;
}
