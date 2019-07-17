package org.sq5nry.plaszczka.frontend;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import org.apache.log4j.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final Logger logger = Logger.getLogger(Controller.class);

    private static final String BACKEND_ROOT_URL = "http://10.0.0.137:8080";
    private static final String BAND = "/bandPassFilter/band/";
    private static final String ATT = "/bandPassFilter/attenuator/";
    private static final String MIXER_SQUARER = "/mixer/squarerThreshold/";
    private static final String MIXER_BIAS = "/mixer/bias/";
    private static final String MIXER_ROOFING = "/mixer/roofingMode/";
    private static final String SELECTIVITY = "/selectivity/";

    @FXML private Label att_disp;
    @FXML private Slider att_reg;

    @FXML private Slider mix_squarer;
    @FXML private Slider mix_bias;
    @FXML private Label mix_squarer_disp;
    @FXML private Label mix_bias_disp;
    
    @FXML private ComboBox mix_roof;
    @FXML private ComboBox selectivity;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug("initialized: " + url + ", rb=" + resourceBundle);
        att_reg.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttenuation());

        mix_squarer.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerSquarer());
        mix_bias.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerBias());
    }

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

    private void sendRequest(String url) {
        logger.debug("sendRequest: " + url);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            logger.debug("response code: " + con.getResponseCode());
        } catch (Exception e) {
            logger.warn("error sending request to backend", e);
        }
    }
}
