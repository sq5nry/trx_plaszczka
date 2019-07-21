package org.sq5nry.plaszczka.frontend;

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
import org.apache.log4j.Logger;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final Logger logger = Logger.getLogger(Controller.class);

    public static final String BACKEND_ROOT_URL = "http://10.0.0.137:8080";
    private BackendCommunicator comm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comm = new BackendCommunicator(BACKEND_ROOT_URL);

        logger.debug("initialized: " + url + ", rb=" + resourceBundle);
        att_reg.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttenuation());

        mix_squarer.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerSquarer());
        mix_bias.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setMixerBias());
        audio_l_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume((int) audio_l_vol.getValue(), Channel.L));
        audio_r_vol.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVolume((int) audio_r_vol.getValue(), Channel.R));
        audio_input.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAudioInput(newVal.toString()));
        vga_ifGain.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setIfGain(newVal.toString()));
        vga_vloop.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVLoop(newVal.toString()));
        freq_slider_mhz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
        freq_slider_khz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
        freq_slider_hz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
    }

    @FXML
    private void freqChanged(ActionEvent e) {
        logger.debug("freqChanged");
    }

    /*
     * Freq control
     */
    @FXML Slider freq_slider_mhz;
    @FXML Slider freq_slider_khz;
    @FXML Slider freq_slider_hz;
    @FXML TextField freq_mhz;
    @FXML TextField freq_khz;
    @FXML TextField freq_hz;



    /*
     * IF amp
     */
    @FXML Slider vga_ifGain;
    @FXML TextField vga_ifGain_disp;

    @FXML Slider vga_vloop;
    @FXML TextField vga_vloop_disp;

    @FXML
    private void vgaOpenLoopRequested(ActionEvent event) {
        comm.sendRequest(BackendCommunicator.IFAMP_VLOOP + "131");
        boolean isOpen = ((ToggleButton)event.getSource()).isSelected();
        vga_vloop.setDisable(isOpen);   //deactivate manual gain control
        if (isOpen) {
            vga_vloop_disp.textProperty().setValue("131");
        } else {
            setIfGain("");  //TODO fix not setting previous value after deactivation
        }
    }

    private void setIfGain(String xxx) {    //TODO param needed?
        float gain = (float) vga_ifGain.getValue();
        comm.sendRequest(BackendCommunicator.IFAMP_MAXIMUMGAIN + gain);
        vga_ifGain_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(gain));
    }

    private void setVLoop(String xxx) {    //TODO param needed?
        float val = (float) vga_vloop.getValue();
        comm.sendRequest(BackendCommunicator.IFAMP_VLOOP + val);
        vga_vloop_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(val));
    }

    @FXML private CheckBox vga_mute;
    @FXML
    private void vgaMuteRequested() {
        comm.sendRequest(BackendCommunicator.IFAMP_MUTE + vga_mute.isSelected());
    }

    @FXML private CheckBox vga_hangOnTx;
    @FXML
    private void vgaHangOnTxRequested() {
        comm.sendRequest(BackendCommunicator.IFAMP_HANGONTRANSMIT + vga_hangOnTx.isSelected());
    }

    /*
     * Audio
     */
    @FXML private Slider audio_l_vol;
    @FXML private Slider audio_r_vol;
    @FXML private CheckBox audio_out_speaker;
    @FXML private CheckBox audio_out_headphones;
    @FXML private CheckBox audio_out_rec;
    @FXML private ChoiceBox audio_input;

    boolean audioLRCoupled = true;
    String lastChannel;
    int lastLvol, lastRvol;
    private void setVolume(int vol, Channel channel) {
        if (audioLRCoupled) {
            if (channel == Channel.L) { //TODO check coupler
                audio_r_vol.setValue(vol);
            } else {
                audio_l_vol.setValue(vol);
            }

            if (lastRvol != -vol && lastLvol != -vol) {
                comm.sendRequest(BackendCommunicator.AUDIO_VOL + "BOTH/" + (-vol));
                lastChannel = "BOTH";
                lastLvol = lastRvol = -vol;
            }

        } else {
            if (channel == Channel.L && (lastLvol != -vol)) {
                comm.sendRequest(BackendCommunicator.AUDIO_VOL + "LEFT/" + (-vol));
                lastChannel = "LEFT";
                lastLvol = -vol;
            }
            if (channel == Channel.R && (lastRvol != -vol)) {
                comm.sendRequest(BackendCommunicator.AUDIO_VOL + "RIGHT/" + (-vol));
                lastChannel = "RIGHT";
                lastRvol = -vol;
            }
        }
    }

    private enum Channel { L, R }

    @FXML
    private void audioCouplerRequested(ActionEvent event) {
        logger.debug("audioCouplerRequested: " + event);
        audioLRCoupled = ((CheckBox) event.getSource()).isSelected();
        logger.debug("audioLRCoupled: " + audioLRCoupled);
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
        comm.sendRequest(BackendCommunicator.AUDIO_INPUT + r);
    }

    @FXML
    private void audioOutChanged(ActionEvent event) {
        logger.debug("audioOutChanged: " + event);
        StringBuffer buf = new StringBuffer("_");
        if (audio_out_headphones.isSelected()) buf.append("head_");
        if (audio_out_rec.isSelected()) buf.append("rec_");
        if (audio_out_speaker.isSelected()) buf.append("speaker_");
        comm.sendRequest(BackendCommunicator.AUDIO_OUTPUT + buf.toString());
    }

    @FXML CheckBox audio_mute;
    @FXML
    private void audioMuteRequested(ActionEvent event) {
        if (((CheckBox)event.getSource()).isSelected()) {
            setVolume(-96, Channel.L);  //TODO
        } else {
            setVolume(lastLvol, Channel.L);
            setVolume(lastRvol, Channel.R);
        }
    }

    /*
     * Mixer
     */
    @FXML private Slider mix_squarer;
    @FXML private Slider mix_bias;
    @FXML private Label mix_squarer_disp;
    @FXML private Label mix_bias_disp;
    @FXML private ComboBox mix_roof;

    public static final DecimalFormat DEC_FORMAT_2DIG = new DecimalFormat("#.00");
    private void setMixerSquarer() {
        float fill = (float) mix_squarer.getValue();
        comm.sendRequest(BackendCommunicator.MIXER_SQUARER + fill);
        mix_squarer_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(fill) + "%");
    }

    private void setMixerBias() {
        float bias = (float) mix_bias.getValue();
        comm.sendRequest(BackendCommunicator.MIXER_BIAS + bias);
        mix_bias_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(bias) + "V");
    }

    @FXML
    private void mixRoofingChanged(ActionEvent event) {
        logger.debug("mixRoofingChanged: " + event);
        String roof = (String) mix_roof.getValue();
        comm.sendRequest(BackendCommunicator.MIXER_ROOFING + roof);
    }

    /*
     * Attenuator
     */
    @FXML private Label att_disp;
    @FXML private Slider att_reg;

    private int prevAtt = 0;
    private void setAttenuation() {
        int att = (int) att_reg.getValue();
        att -= att % 2; //adjust to 2dB step
        if (att != prevAtt) comm.sendRequest(BackendCommunicator.ATT + att);
        prevAtt = att;
        att_disp.textProperty().setValue(att + "dB");
    }

    /*
     * BFP
     */
    @FXML
    private void bpfBandChanged(ActionEvent event) {
        logger.debug("bpfBandChanged: " + event);
        String band = ((RadioButton) event.getSource()).getId().substring(4);
        comm.sendRequest(BackendCommunicator.BAND + band);
    }

    /*
     * Selectivity
     */
    @FXML private ComboBox selectivity;

    @FXML
    private void selectivityChanged(ActionEvent event) {
        logger.debug("selectivityChanged: " + event);
        String bw = (String) selectivity.getValue();
        comm.sendRequest(BackendCommunicator.SELECTIVITY + bw);
    }

    /*
     * Detector
     */
    @FXML
    private void detectorStateChanged(ActionEvent event) {
        logger.debug("detectorStateChanged: " + event);
        comm.sendRequest(BackendCommunicator.DETECTOR_ENA + ((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    private void detectorModeChanged(ActionEvent event) {
        logger.debug("detectorModeChanged: " + event);
        comm.sendRequest(BackendCommunicator.DETECTOR_MODE + ((ComboBox) event.getSource()).getValue());
    }

    /*
     * Unit blocks
     */
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

    @FXML
    private void reinitialize(ActionEvent event) {
        Map response = comm.queryState();
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
        if (state.contains("INITIALIZED")) {
            unit.setFill(Color.GREENYELLOW);
        } else {
            unit.setFill(Color.RED);
        }
    }

    private void setUnitColor(Labeled label, String state) {
        if (state.contains("INITIALIZED")) {
            label.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            label.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }
}
