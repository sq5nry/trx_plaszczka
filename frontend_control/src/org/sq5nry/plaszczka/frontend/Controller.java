package org.sq5nry.plaszczka.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    String backend = "http://10.0.0.137:8080/bandPassFilter/band/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialized: " + url + ", rb=" + resourceBundle);
    }

    private void enableBpf(String band) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(backend + band).openConnection();
            con.setRequestMethod("GET");
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void enable160m(ActionEvent event) {
        System.out.println("160m " + event);
        enableBpf("160m");

    }

    @FXML
    private void enable80m(ActionEvent event) {
        System.out.println("80m " + event);
        enableBpf("80m");
    }

    @FXML
    private void enable60m(ActionEvent event) {
        System.out.println("60m " + event);
        enableBpf("60m");
    }

    @FXML
    private void enable40m(ActionEvent event) {
        System.out.println("40m " + event);
        enableBpf("40m");
    }

    @FXML
    private void enable30m(ActionEvent event) {
        System.out.println("30m " + event);
        enableBpf("30m");
    }
}
