package org.sq5nry.plaszczka.backend.api.audio;

import java.io.IOException;

public interface AfAmplifier {
    String ROOT = "/audio";
    String RESOURCE_PATH_INPUT = ROOT + "/input/{mode}";
    String RESOURCE_PATH_VOLUME = ROOT + "/volume/{channel}/{volume}";
    String RESOURCE_PATH_MUTE_LOUDNESS = ROOT + "/muteLoudness/{mode}";
    String RESOURCE_PATH_OUTPUT_AMPLIFIER = ROOT + "/output/{output}";

    /**
     * Set input channel routing from IQ input to stereo amplifier.
     * @param mode
     * @return result
     */
    String setInput(InputSelector mode) throws IOException;

    /**
     * Set volume.
     * @param channel
     * @param volume -dB, 0..-95dB
     */
    String setVolume(Channel channel, Integer volume) throws IOException;

    /**
     * Set loud or mute effects.
     * @param loudness
     * @return result
     */
    String setMuteLoudness(MuteAndLoudness loudness) throws IOException;

    /**
     * Set output amplifier(s).
     * @param amp which output amplifier(s) is(are) enabled
     * @return result
     */
    String setOutputAmplifier(OutputAmplifier amp) throws IOException;
}
