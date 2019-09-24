package org.sq5nry.plaszczka.backend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.api.inputfilter.Attenuator;
import org.sq5nry.plaszczka.backend.api.mgmt.ReceiverCtrl;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.api.selectivity.Selectivity;
import org.sq5nry.plaszczka.backend.api.synthesiser.Synthesizer;
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.client.communicators.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class BackendCommunicator implements RequestSender {
    private static final Logger logger = Logger.getLogger(BackendCommunicator.class);

    public static final String BACKEND_HOST_LOCAL = "127.0.0.1";
    public static final String BACKEND_HOST_REAL = "10.0.0.139";
    public static final String BACKEND_BANANA = "10.0.0.141";
    public static final String BACKEND_RPI1A = "10.0.0.180";
    public static final String BACKEND_HOST_MOST_REAL = "sq9nry.no-ip.org";
    public static final String BACKEND_PORT = "8090";

    private static final String BACKEND_HOST = BACKEND_HOST_REAL;
    private static final String BACKEND_ROOT_URL = "http://" + BACKEND_HOST + ":" + BACKEND_PORT;

    private AfAmplifier afAmplifier;
    private Detector detector;
    private FrequencyDisplay frequencyDisplay;
    private Attenuator attenuator;
    private HModeMixer hModeMixer;
    private Selectivity selectivity;
    private Synthesizer synthesizer;
    private IfAmp ifAmp;
    private ReceiverCtrl receiverCtrl;

    private String rootUrl;

    public BackendCommunicator(String rootUrl) {
        this.rootUrl = rootUrl;
        afAmplifier = new AfAmplifierCommunicator(this);
        detector = new DetectorCommunicator(this);
        frequencyDisplay = new FrequencyDisplayCommunicator(this);
        attenuator = new AttenuatorCommunicator(this);
        receiverCtrl = new ReceiverCtrlCommunicator(this);
        hModeMixer = new HModeMixerCommunicator(this);
        selectivity = new SelectivityCommunicator(this);
        synthesizer = new SynthesizerCommunicator(this);
        ifAmp = new IfAmpCommunicator(this);
    }

    @Override
    public String sendRequest(String path) throws IOException {
        logger.debug("sendRequest: " + path);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1_000)   //timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.
                .setSocketTimeout(2_000)    //socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
                .setConnectionRequestTimeout(2_000) //timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.
                .build();

        HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

        HttpGet request = new HttpGet(rootUrl + path);
        request.addHeader("User-Agent", USER_AGENT);
        final HttpResponse response;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.warn("error sending request to backend=" + e.getLocalizedMessage());
            return "";
        }
        logger.debug("response code: " + response.getStatusLine().getStatusCode());
        return response.getEntity().getContent().readAllBytes().toString();
    }

    public Map queryState() {
        String url = rootUrl + "INITIALIZE";    //TODO URL PATH?
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

    public Detector getDetector() {
        return detector;
    }

    public AfAmplifier getAfAmplifier() {
        return afAmplifier;
    }

    public FrequencyDisplay getFrequencyDisplay() {
        return frequencyDisplay;
    }

    public Attenuator getAttenuator() {
        return attenuator;
    }

    public ReceiverCtrl getReceiverCtrl() {
        return receiverCtrl;
    }

    public HModeMixer gethModeMixer() {
        return hModeMixer;
    }

    public Selectivity getSelectivity() {
        return selectivity;
    }

    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    public IfAmp getIfAmp() {
        return ifAmp;
    }
}
