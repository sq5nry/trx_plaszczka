package org.sq5nry.plaszczka.backend.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.sq5nry.plaszczka.backend.api.audio.*;
import org.sq5nry.plaszczka.backend.api.detector.Detector;
import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.api.inputfilter.Attenuator;
import org.sq5nry.plaszczka.backend.api.inputfilter.BandPassFilter;
import org.sq5nry.plaszczka.backend.api.mgmt.ReceiverCtrl;
import org.sq5nry.plaszczka.backend.api.mixer.HModeMixer;
import org.sq5nry.plaszczka.backend.api.selectivity.Selectivity;
import org.sq5nry.plaszczka.backend.api.synthesiser.Synthesizer;
import org.sq5nry.plaszczka.backend.api.vga.IfAmp;
import org.sq5nry.plaszczka.backend.client.communicators.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class BackendCommunicator implements RequestSender, ExceptionHandler {
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
    private ExceptionHandler exceptionHandler;
    private BandPassFilter inputBPFComminicator;

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
        inputBPFComminicator = new InputBPFComminicator(this);
        exceptionHandler = this;
    }

    public BackendCommunicator(String host, int port) {
        this("http://" + host + ":" + port);
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public String getRootURL() {
        return rootUrl;
    }

    @Override
    public String sendRequest(String path){
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
        final HttpEntity entity = response.getEntity();
        final ContentType ct = ContentType.getOrDefault(entity);
        final StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(entity.getContent(), writer, ct.getCharset());
            return writer.toString();
        } catch (IOException e) {
            return exceptionHandler.handleException(e);
        }
    }

    public String handleException(IOException e) {
        logger.warn("exception while executing a backend request", e);
        return e.getLocalizedMessage();
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

    public BandPassFilter getBandPassFilter() {
        return inputBPFComminicator;
    }

    public ReceiverCtrl getReceiverCtrl() {
        return receiverCtrl;
    }
}
