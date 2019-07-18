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
import org.apache.http.impl.client.DefaultHttpClient;
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
    private static final String AUDIO_VOL = "/audio/volume/";
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug("initialized: " + url + ", rb=" + resourceBundle);
        att_reg.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttenuation());

        mix_squarer.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerSquarer());
        mix_bias.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerBias());
        audio_l_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume(audio_l_vol, Channel.L));
        audio_r_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume(audio_r_vol, Channel.R));
    }

    private void setVolume(Slider slider, Channel channel) {
        int vol = (int) slider.getValue();
        if (channel == Channel.L) { //TODO check coupler
            audio_r_vol.setValue(vol);
        } else {
            audio_l_vol.setValue(vol);
        }
        sendRequest(BACKEND_ROOT_URL + AUDIO_VOL + "/BOTH/" + (-vol));
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

    @FXML
    private void reinitialize(ActionEvent event) {
        Map response = sendRequest(BACKEND_ROOT_URL + INITIALIZE);
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

    public void shutdown() {
        logger.debug("shutting down");
        client.getConnectionManager().shutdown();
    }

    HttpClient client = null;
    private Map sendRequest(String url) {
        logger.debug("sendRequest: " + url);

        client = new DefaultHttpClient();
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
