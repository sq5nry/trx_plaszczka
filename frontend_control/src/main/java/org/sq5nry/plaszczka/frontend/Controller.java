package org.sq5nry.plaszczka.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class Controller implements Initializable {
    private static final Logger logger = Logger.getLogger(Controller.class);

    private static final String BACKEND_ROOT_URL = "http://10.0.0.137:8080";
    private static final String BAND = "/bandPassFilter/band/";
    private static final String ATT = "/bandPassFilter/attenuator/";
    private static final String MIXER_SQUARER = "/mixer/squarerThreshold/";
    private static final String MIXER_BIAS = "/mixer/bias/";
    private static final String MIXER_ROOFING = "/mixer/roofingMode/";
    private static final String SELECTIVITY = "/selectivity/";
    private static final String DETECTOR_ENA = "/detector/enabled/";
    private static final String DETECTOR_MODE = "/detector/mode/";
    private static final String AUDIO_VOL = "/audio/volume/";
    private static final String AUDIO_OUTPUT = "/audio/output/";
    private static final String AUDIO_INPUT = "/audio/input/";
    private static final String INITIALIZE = "/mgmt/initialize/rx";

    @FXML private Label att_disp;
    @FXML private Slider att_reg;

    @FXML private Slider mix_squarer;
    @FXML private Slider mix_bias;
    @FXML private Label mix_squarer_disp;
    @FXML private Label mix_bias_disp;
    
    @FXML private ComboBox mix_roof;
    @FXML private ComboBox selectivity;

    @FXML private Slider audio_l_vol;
    @FXML private Slider audio_r_vol;

    @FXML private Rectangle box_att;
    @FXML private Rectangle box_bpf;
    @FXML private Circle box_mixer;
    @FXML private Rectangle box_mixRoofing;
    @FXML private Rectangle box_selectivity;
    @FXML private Polygon box_vga;
    @FXML private Rectangle box_detRoofing;
    @FXML private Rectangle box_detector;
    @FXML private Polygon box_audio;
    @FXML private Label box_disp;

    @FXML private CheckBox audio_out_speaker;
    @FXML private CheckBox audio_out_headphones;
    @FXML private CheckBox audio_out_rec;

    @FXML private ChoiceBox audio_input;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug("initialized: " + url + ", rb=" + resourceBundle);
        att_reg.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttenuation());

        mix_squarer.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerSquarer());
        mix_bias.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerBias());
        audio_l_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume(audio_l_vol, Channel.L));
        audio_r_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume(audio_r_vol, Channel.R));
        audio_input.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAudioInput(newVal.toString()));
    }

    boolean audioLRCoupled = true;
    String lastChannel;
    int lastLvol, lastRvol;
    private void setVolume(Slider slider, Channel channel) {
        int vol = (int) slider.getValue();
        if (audioLRCoupled) {
            if (channel == Channel.L) { //TODO check coupler
                audio_r_vol.setValue(vol);
            } else {
                audio_l_vol.setValue(vol);
            }

            if (lastRvol != -vol && lastLvol != -vol) {
                sendRequest(BACKEND_ROOT_URL + AUDIO_VOL + "BOTH/" + (-vol));
                lastChannel = "BOTH";
                lastLvol = lastRvol = -vol;
            }

        } else {
            if (channel == Channel.L && (lastLvol != -vol)) {
                sendRequest(BACKEND_ROOT_URL + AUDIO_VOL + "LEFT/" + (-vol));
                lastChannel = "LEFT";
                lastLvol = -vol;
            }
            if (channel == Channel.R && (lastRvol != -vol)) {
                sendRequest(BACKEND_ROOT_URL + AUDIO_VOL + "RIGHT/" + (-vol));
                lastChannel = "RIGHT";
                lastRvol = -vol;
            }
        }
    }

    private enum Channel { L, R }

    public static final DecimalFormat DEC_FORMAT_2DIG = new DecimalFormat("#.00");
    private void setMixerSquarer() {
        float fill = (float) mix_squarer.getValue();
        sendRequest(BACKEND_ROOT_URL + MIXER_SQUARER + fill);
        mix_squarer_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(fill) + "%");
    }

    private void setMixerBias() {
        float bias = (float) mix_bias.getValue();
        sendRequest(BACKEND_ROOT_URL + MIXER_BIAS + bias);
        mix_bias_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(bias) + "V");
    }

    private int prevAtt = 0;
    private void setAttenuation() {
        int att = (int) att_reg.getValue();
        att -= att % 2; //adjust to 2dB step
        if (att != prevAtt) sendRequest(BACKEND_ROOT_URL + ATT + att);
        prevAtt = att;
        att_disp.textProperty().setValue(att + "dB");
    }

    @FXML
    private void audioCouplerRequested(ActionEvent event) {
        logger.debug("audioCouplerRequested: " + event);
        audioLRCoupled = ((CheckBox) event.getSource()).isSelected();
        logger.debug("audioLRCoupled: " + audioLRCoupled);
    }

    @FXML
    private void bpfBandChanged(ActionEvent event) {
        logger.debug("bpfBandChanged: " + event);
        String band = ((RadioButton) event.getSource()).getId().substring(4);
        sendRequest(BACKEND_ROOT_URL + BAND + band);
    }

    @FXML
    private void selectivityChanged(ActionEvent event) {
        logger.debug("selectivityChanged: " + event);
        String bw = (String) selectivity.getValue();
        sendRequest(BACKEND_ROOT_URL + SELECTIVITY + bw);
    }

    @FXML
    private void mixRoofingChanged(ActionEvent event) {
        logger.debug("mixRoofingChanged: " + event);
        String roof = (String) mix_roof.getValue();
        sendRequest(BACKEND_ROOT_URL + MIXER_ROOFING + roof);
    }

    private void setAudioInput(String newVal) {
        logger.debug("setAudioInput: " + newVal);
        String r = "dupa";  //TODO
        switch(newVal) {
            case "Q mono": r = "QQ_MONO"; break;
            case "I mono": r = "II_MONO"; break;
            case "I/Q stereo": r = "IQ_STEREO"; break;
            case "I/Q off": r = "IQ_OFF"; break;
        }
        sendRequest(BACKEND_ROOT_URL + AUDIO_INPUT + r);
    }

    @FXML
    private void audioOutChanged(ActionEvent event) {
        logger.debug("audioOutChanged: " + event);
        StringBuffer buf = new StringBuffer();
        if (audio_out_headphones.isSelected()) buf.append("_head");
        if (audio_out_rec.isSelected()) buf.append("_rec");
        if (audio_out_speaker.isSelected()) buf.append("_speaker");
        sendRequest(BACKEND_ROOT_URL + AUDIO_OUTPUT + buf.toString());
    }

    @FXML
    private void detectorStateChanged(ActionEvent event) {
        logger.debug("detectorStateChanged: " + event);
        sendRequest(BACKEND_ROOT_URL + DETECTOR_ENA + ((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    private void detectorModeChanged(ActionEvent event) {
        logger.debug("detectorModeChanged: " + event);
        sendRequest(BACKEND_ROOT_URL + DETECTOR_MODE + ((ComboBox) event.getSource()).getValue());
    }

    @FXML
    private void reinitialize(ActionEvent event) {
        Map response = queryBackend(BACKEND_ROOT_URL + INITIALIZE);
        for (Object name: response.keySet()) {
            String unitName = (String) name;
            String state = (String) response.get(name);
            if ("AudioUnit".equals(unitName)) {
                setUnitColor(box_audio, state);
            } else if ("BpfUnit".equals(unitName)) {
                setUnitColor(box_att, state);
                setUnitColor(box_bpf, state);
            } else if ("QSDUnit".equals(unitName)) {
                setUnitColor(box_detector, state);
                setUnitColor(box_detRoofing, state);
            } else if ("FrontEndMixerUnit".equals(unitName)) {
                setUnitColor(box_mixer, state);
                setUnitColor(box_mixRoofing, state);
            } else if ("NixieDisplayUnit".equals(unitName)) {
                setUnitColor(box_disp, state);
            } else if ("SelectivityUnit".equals(unitName)) {
                setUnitColor(box_selectivity, state);
            } else if ("VgaUnit".equals(unitName)) {
                setUnitColor(box_vga, state);
            }
        }
        logger.debug("response=" + response.values());
    }

    private void setUnitColor(Shape unit, String state) {
        if ("INITIALIZED".equals(state)) {
            unit.setFill(Color.GREENYELLOW);
        } else {
            unit.setFill(Color.RED);
        }
    }

    private void setUnitColor(Labeled label, String state) {
        if ("INITIALIZED".equals(state)) {
            label.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            label.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    private void sendRequest(String url) {
        logger.debug("sendRequest: " + url);

        //if (client == null) {   //TODO opt.
        HttpClient client = HttpClientBuilder.create().build();
        //}
        HttpGet request = new HttpGet(url);

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

    private Map queryBackend(String url) {
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
        logger.debug("response code: " + response.getStatusLine().getStatusCode());
        return new HashMap();
    }
}
