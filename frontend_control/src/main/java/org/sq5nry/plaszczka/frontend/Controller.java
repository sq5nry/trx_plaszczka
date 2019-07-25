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

        vga_ifGain.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setIfGain());
        vga_vloop.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVLoop());
        vga_Vleak.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVleak());
        vga_Attack.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setAttack());
        vga_Vspd.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVspd());
        vga_Vsph.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVsph());
        vga_Vspa.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> setVspa());

        freq_slider_mhz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
        freq_slider_khz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
        freq_slider_hz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
    }

    @FXML private void setDefaultsRequested(ActionEvent event) {
        setAttenuation();
        bpf_20m.fire();
        setMixerSquarer();
        setMixerBias();
        setDispFreq();
        setMixRoofing();

        //setSelectivity();
        comm.sendRequest(BackendCommunicator.SELECTIVITY + "2400"); //workaround

        setIfGain();
        setVLoop();
        vgaMuteRequested();
        vgaHangOnTxRequested();
        setVsph();
        setVspa();
        setVleak();
        setAttack();
        setVspd();
        setVfloor();
        setVath();
        setVhth();
        setDetectorMode();
        comm.sendRequest(BackendCommunicator.DETECTOR_ENA + "1");   //workaround
        setVolume(0, Channel.R);
        setVolume(0, Channel.L);
        setAudioInput("I/Q stereo");
        setAudioOut();
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
    @FXML RadioButton mixing_h;
    @FXML RadioButton mixing_l;

    public static final DecimalFormat DEC_FORMAT_3DIG = new DecimalFormat("000");
    public static final DecimalFormat DEC_FORMAT_2DIG = new DecimalFormat("#00");

    public static final int LO_FREQ = 9000000;

    @FXML
    private void freqChanged(ActionEvent e) {
        setDispFreq();
    }

    private void setDispFreq() {
        logger.debug("freqChanged");
        String MHz = DEC_FORMAT_2DIG.format(freq_slider_mhz.getValue());
        freq_mhz.textProperty().setValue(MHz);
        String kHz = DEC_FORMAT_3DIG.format(freq_slider_khz.getValue());
        freq_khz.textProperty().setValue(kHz);
        String Hz = DEC_FORMAT_3DIG.format(freq_slider_hz.getValue());
        freq_hz.textProperty().setValue(Hz);

        String freqString = MHz + kHz + Hz;
        int freq = Integer.parseInt(freqString);
        if (mixing_h.isSelected()) {
            freq += LO_FREQ;
        } else if (mixing_l.isSelected()) {
            freq = Math.abs(freq - LO_FREQ);
        }
        comm.sendRequest(BackendCommunicator.LO_DDS + freq);
        comm.sendRequest(BackendCommunicator.FREQ_DISPLAY + freqString);
    }

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
            setIfGain();  //TODO fix not setting previous value after deactivation
        }
    }

    private void setIfGain() {
        float gain = (float) vga_ifGain.getValue();
        comm.sendRequest(BackendCommunicator.IFAMP_MAXIMUMGAIN + gain);
        vga_ifGain_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(gain));
    }

    private void setVLoop() {
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

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vhth;
    @FXML
    private void vgaVhthChanged(ActionEvent event) {
        setVhth();
    }
    private void setVhth() {
        comm.sendRequest(BackendCommunicator.IFAMP_HANGTHRESHOLD + vga_Vhth.textProperty().getValue());
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vath;
    @FXML
    private void vgaVathChanged(ActionEvent event) {
        setVath();

    }
    private void setVath() {
        comm.sendRequest(BackendCommunicator.IFAMP_STRATEGYTHRESHOLD + vga_Vath.textProperty().getValue());
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vfloor;
    @FXML
    private void vgaVfloorChanged(ActionEvent event) {
        setVfloor();
    }
    private void setVfloor() {
        comm.sendRequest(BackendCommunicator.IFAMP_NOISEFLOORCOMPENSATION + vga_Vfloor.textProperty().getValue());
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vspa;
    @FXML TextField vga_Vspa_disp;
    @FXML
    private void vgaVspaChanged(ActionEvent event) {
        setVspa();
    }
    private void setVspa() {
        String val = DEC_FORMAT_2DIG.format(vga_Vspa.getValue());
        vga_Vspa_disp.textProperty().setValue(val);
        comm.sendRequest(BackendCommunicator.IFAMP_DECAYSPEEDFORATTACKDECAYMODE + val);
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vsph;
    @FXML TextField vga_Vsph_disp;
    @FXML
    private void vgaVsphChanged(ActionEvent event) {
        setVsph();
    }
    private void setVsph() {
        String val = DEC_FORMAT_2DIG.format(vga_Vsph.getValue());
        vga_Vsph_disp.textProperty().setValue(val);
        comm.sendRequest(BackendCommunicator.IFAMP_DECAYSPEEDINDECAYSTATEFORHANGMODE + val);
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vspd;
    @FXML TextField vga_Vspd_disp;
    @FXML
    private void vgaVspdChanged(ActionEvent event) {
        setVspd();
    }
    private void setVspd() {
        String val = DEC_FORMAT_2DIG.format(vga_Vspd.getValue());
        vga_Vspd_disp.textProperty().setValue(val);
        comm.sendRequest(BackendCommunicator.IFAMP_MAXIMUMHANGTIMEINHANGMODE + val);
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Attack;
    @FXML TextField vga_Attack_disp;
    @FXML
    private void vgaAttackChanged(ActionEvent event) {
        setAttack();
    }
    private void setAttack() {
        String val = DEC_FORMAT_2DIG.format(vga_Attack.getValue());
        vga_Attack_disp.textProperty().setValue(val);
        comm.sendRequest(BackendCommunicator.IFAMP_ATTACKTIME + val);
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vleak;
    @FXML TextField vga_Vleak_disp;
    @FXML
    private void vgaVleakChanged(ActionEvent event) {
        setVleak();
    }
    private void setVleak() {
        String val = DEC_FORMAT_2DIG.format(vga_Vleak.getValue());
        vga_Vleak_disp.textProperty().setValue(val);
        comm.sendRequest(BackendCommunicator.IFAMP_DECAYSPEEDINHANGSTATEFORHANGMODE + val);
    }
    ///////////////////////////////////////////////////////

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
    int lastLvol = 1, lastRvol = 1;
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
        setAudioOut();
    }

    private void setAudioOut() {
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

    public static final DecimalFormat DEC_FORMAT_1_2DIG = new DecimalFormat("#.00");
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
        setMixRoofing();
    }

    private void setMixRoofing() {
        String roof = (String) mix_roof.getValue();
        logger.debug("mixRoofingChanged: set to " + roof);
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

    @FXML RadioButton bpf_20m;

    /*
     * BFP
     */
    @FXML
    private void bpfBandChanged(ActionEvent event) {
        logger.debug("bpfBandChanged: " + event);
        String band = ((RadioButton) event.getSource()).getId().substring(4);
        comm.sendRequest(BackendCommunicator.BAND + band);
        if (band.equals("20m")) {
            freq_slider_mhz.setValue(14);
            freq_slider_mhz.setDisable(true);

            freq_slider_khz.setMajorTickUnit(35);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(350);
        } else if (band.equals("30m")) {
            freq_slider_mhz.setValue(10);
            freq_slider_mhz.setDisable(true);

            freq_slider_khz.setMajorTickUnit(5);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(100);
            freq_slider_khz.setMax(150);
        } else if (band.equals("40m")) {
            freq_slider_mhz.setValue(7);
            freq_slider_mhz.setDisable(true);

            freq_slider_khz.setMajorTickUnit(20);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(200);
        } else if (band.equals("80m")) {
            freq_slider_mhz.setValue(3);
            freq_slider_mhz.setDisable(true);

            freq_slider_khz.setMajorTickUnit(30);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(500);
            freq_slider_khz.setMax(800);
        }
    }

    /*
     * Selectivity
     */
    @FXML private ComboBox selectivity;

    @FXML
    private void selectivityChanged(ActionEvent event) {
        logger.debug("selectivityChanged: " + event);
        setSelectivity();
    }

    private void setSelectivity() {
        comm.sendRequest(BackendCommunicator.SELECTIVITY + selectivity.getValue());
    }

    /*
     * Detector
     */
    @FXML ComboBox det_mode;

    @FXML
    private void detectorStateChanged(ActionEvent event) {
        logger.debug("detectorStateChanged: " + event);
        comm.sendRequest(BackendCommunicator.DETECTOR_ENA + ((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    private void detectorModeChanged(ActionEvent event) {
        logger.debug("detectorModeChanged: " + event);
        setDetectorMode();
    }

    private void setDetectorMode() {
        comm.sendRequest(BackendCommunicator.DETECTOR_MODE + det_mode.getValue());
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
