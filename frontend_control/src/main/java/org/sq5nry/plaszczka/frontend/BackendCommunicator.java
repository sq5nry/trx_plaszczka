package org.sq5nry.plaszczka.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class BackendCommunicator {
    private static final Logger logger = Logger.getLogger(BackendCommunicator.class);

    public static final String BAND = "/bandPassFilter/band/";
    public static final String ATT = "/bandPassFilter/attenuator/";

    public static final String MIXER_SQUARER = "/mixer/squarerThreshold/";
    public static final String MIXER_BIAS = "/mixer/bias/";
    public static final String MIXER_ROOFING = "/mixer/roofingMode/";

    public static final String IFAMP_DECAYSPEEDINDECAYSTATEFORHANGMODE = "/ifAmp/decaySpeedInDecayStateForHangMode/";
    public static final String IFAMP_DECAYSPEEDFORATTACKDECAYMODE = "/ifAmp/decaySpeedForAttackDecayMode/";
    public static final String IFAMP_DECAYSPEEDINHANGSTATEFORHANGMODE = "/ifAmp/decaySpeedInHangStateForHangMode/";
    public static final String IFAMP_NOISEFLOORCOMPENSATION = "/ifAmp/noiseFloorCompensation/";
    public static final String IFAMP_STRATEGYTHRESHOLD = "/ifAmp/strategyThreshold/";
    public static final String IFAMP_HANGTHRESHOLD = "/ifAmp/hangThreshold/";
    public static final String IFAMP_VLOOP = "/ifAmp/VLoop/";
    public static final String IFAMP_MAXIMUMGAIN = "/ifAmp/maximumGain/";
    public static final String IFAMP_MAXIMUMHANGTIMEINHANGMODE = "/ifAmp/maximumHangTimeInHangMode/";
    public static final String IFAMP_ATTACKTIME = "/ifAmp/attackTime/";
    public static final String IFAMP_HANGONTRANSMIT = "/ifAmp/hangOnTransmit/";
    public static final String IFAMP_MUTE = "/ifAmp/mute/";

    public static final String SELECTIVITY = "/selectivity/";

    public static final String DETECTOR_ENA = "/detector/enabled/";
    public static final String DETECTOR_MODE = "/detector/mode/";

    public static final String AUDIO_VOL = "/audio/volume/";
    public static final String AUDIO_OUTPUT = "/audio/output/";
    public static final String AUDIO_INPUT = "/audio/input/";

    public static final String INITIALIZE = "/mgmt/initialize/rx";

    private String rootUrl;

    public BackendCommunicator(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void sendRequest(String path) {
        logger.debug("sendRequest: " + path);

        //if (client == null) {   //TODO opt.
        HttpClient client = HttpClientBuilder.create().build();
        //}
        HttpGet request = new HttpGet(rootUrl + path);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.warn("error sending request to backend", e);
        }
        logger.debug("response code: " + response.getStatusLine().getStatusCode());
    }

    public Map queryState() {
        String url = rootUrl + INITIALIZE;
        logger.debug("sendRequest: " + url);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = null;
        try {
            response = client.execute(request);
            if (response.getEntity().getContentLength() != 0) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.getEntity().getContent(), Map.class);
            }
        } catch (IOException e) {
            logger.warn("error sending request to backend", e);
        }
        logger.debug("response: " + response == null ? "null" : response.getStatusLine());
        return new HashMap();
    }
}
