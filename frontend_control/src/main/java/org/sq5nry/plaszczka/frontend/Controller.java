package org.sq5nry.plaszczka.frontend;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    String backend_band = "http://10.0.0.137:8080/bandPassFilter/band/";
    String backend_att = "http://10.0.0.137:8080/bandPassFilter/attenuator/";

    @FXML private Label att_disp;
    @FXML private Slider att_reg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.finest("initialized: " + url + ", rb=" + resourceBundle);
        att_reg.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttenuation());
    }

    private int prevAtt = 0;
    private void setAttenuation() {
        int att = (int) att_reg.getValue();
        att -= att % 2; //adjust to 2dB step
        if (att != prevAtt) sendRequest(backend_att + att);
        prevAtt = att;
        att_disp.textProperty().setValue(att + "dB");
    }

    @FXML
    private void bpfBandChanged(ActionEvent event) {
        logger.finest("bpfBandChanged: " + event);
        String band = ((RadioButton) event.getSource()).getId().substring(4);
        enableBpf(band);
    }

    private void enableBpf(String band) {
        String url = backend_band + band;
        logger.finer("backend url: " + url);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            logger.finest("response code: " + con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            logger.finest("response code: " + con.getResponseCode());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error sending request to backend", e);
        }
    }
}
