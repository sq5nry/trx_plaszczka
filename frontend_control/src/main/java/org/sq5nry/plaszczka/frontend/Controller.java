package org.sq5nry.plaszczka.frontend;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.apache.log4j.Logger;
import org.sq5nry.plaszczka.backend.api.Mode;
import org.sq5nry.plaszczka.backend.api.audio.Channel;
import org.sq5nry.plaszczka.backend.api.audio.MuteAndLoudness;
import org.sq5nry.plaszczka.backend.client.BackendCommunicator;
import org.sq5nry.plaszczka.backend.common.Unit;
import org.sq5nry.plaszczka.frontend.comm.VAgcStreamController;
import org.sq5nry.plaszczka.frontend.comm.WsStompClient;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable, MessageHandler.Whole<String> {
    private static final Logger logger = Logger.getLogger(Controller.class);

    public static final String BACKEND_HOST_LOCAL = "127.0.0.1";
    public static final String BACKEND_HOST_REAL = "10.0.0.139";
    public static final String BACKEND_BANANA = "10.0.0.141";
    public static final String BACKEND_RPI1A = "10.0.0.180";
    public static final String BACKEND_HOST_MOST_REAL = "sq9nry.no-ip.org";
    public static final String BACKEND_PORT = "8090";

    private static final String BACKEND_HOST = BACKEND_HOST_REAL;
    private static final String BACKEND_ROOT_URL = "http://" + BACKEND_HOST + ":" + BACKEND_PORT;
    private static final String BACKEND_STOMP_URL = "ws://" + BACKEND_HOST + ":" + BACKEND_PORT + "/vagc-websocket";

    private static final int VAGC_READ_PERIOD = 100;
    private BackendCommunicator comm;
    private VAgcStreamController ifMsg;

    @FXML
    private Button reinit;

    @FXML
    private GridPane mainGrid;

    private List<Node> allControls = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        s_meter.prefWidthProperty().bind(pbox.widthProperty().subtract(20));

        comm = new BackendCommunicator(BACKEND_ROOT_URL);
        Map<String, Unit.State> result = comm.getReceiverCtrl().getState();
        if (result.size() == 0) {
            logger.warn("backend is down");
        } else {
            logger.warn("TODO: " + result);
        }

        ifMsg = new WsStompClient(BACKEND_STOMP_URL).initialize();
        ifMsg.addMessageHandler(this);
        ifMsg.setPeriod(VAGC_READ_PERIOD);
        ifMsg.connect();

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

        freq_slider_khz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> freqChanged(null));
        freq_slider_hz.valueProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> fineAdjustHz());

        logger.info("graying out");
        getControllables(mainGrid, allControls);
        allControls.remove(reinit);

        disableAllControls(true);//TODO
    }

    private void disableAllControls(boolean isDisabled) {
        for(Node ctl: allControls) {
            ctl.setDisable(isDisabled);
        }
    }

    public void getControllables(Pane root, List<Node> result) {
        ObservableList<Node> nodes = root.getChildren();
        for (Node node: nodes) {
            if (node instanceof Control) {
                result.add(node);
            } else if (node instanceof GridPane) {
                getControllables((Pane) node, result);
            }
        }
    }

    @FXML private void setDefaultsRequested(ActionEvent event) {
        setAttenuation();
        bpf_20m.fire();
        setMixerSquarer();
        setMixerBias();
        setDispFreq();
        setMixRoofing();

        selectivity.getSelectionModel().select(3);
        setSelectivity();
        det_ena.setSelected(true);

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

        audio_input.getSelectionModel().selectFirst();
        setAudioInput("I/Q stereo");
        setAudioOut();
    }

    /*
     * Freq control
     */
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
    private void freqChanged(ActionEvent e) throws IOException {
        setDispFreq();
    }

    String MHz = "14";

    private void setDispFreq() throws IOException {
        logger.debug("freqChanged");

        double khz_raw = freq_slider_khz.getValue();
        int int_khz = (int) khz_raw;
        double fract_khz = khz_raw - int_khz;
        String Hz = DEC_FORMAT_3DIG.format(fract_khz * 1000);

        freq_slider_hz.setValue(fract_khz * 1000);

        setFreq0(Hz, int_khz);
    }

    private void setFreq0(String Hz, int int_khz) throws IOException {
        freq_mhz.textProperty().setValue(MHz);

        String kHz = DEC_FORMAT_3DIG.format(int_khz);
        freq_khz.textProperty().setValue(kHz);

        freq_hz.textProperty().setValue(Hz);

        String freqString = MHz + kHz + Hz;
        int freq = Integer.parseInt(freqString);
        if (mixing_h.isSelected()) {
            freq += LO_FREQ;
        } else if (mixing_l.isSelected()) {
            freq = Math.abs(freq - LO_FREQ);
        }
        comm.getSynthesizer().setVfoFrequency(freq);
        comm.getFrequencyDisplay().setFrequency(Integer.parseInt(freqString));
    }

    private void fineAdjustHz() throws IOException {
        double hz_raw = freq_slider_hz.getValue();
        String Hz = DEC_FORMAT_3DIG.format(hz_raw);
        freq_hz.textProperty().setValue(Hz);

        double khz_raw = freq_slider_khz.getValue();
        int int_khz = (int) khz_raw;
        setFreq0(Hz, int_khz);
    }

    @FXML
    private void bfoChanged(ActionEvent e) {
        logger.debug("bfoChanged");
    }

    /*
     * IF amp
     */
    @FXML HBox pbox;
    @FXML ProgressBar s_meter;
    @FXML Slider vga_ifGain;
    @FXML TextField vga_ifGain_disp;

    @FXML Slider vga_vloop;
    @FXML TextField vga_vloop_disp;

    @FXML
    private void vgaOpenLoopRequested(ActionEvent event) throws Exception {
        comm.getIfAmp().setVLoop(131);
        boolean isOpen = ((ToggleButton)event.getSource()).isSelected();
        vga_vloop.setDisable(isOpen);   //deactivate manual gain control
        if (isOpen) {
            vga_vloop_disp.textProperty().setValue("131");
        } else {
            setIfGain();  //TODO fix not setting previous value after deactivation
        }
    }

    private void setIfGain() throws Exception {
        float gain = (float) vga_ifGain.getValue();
        comm.getIfAmp().setMaximumGain(gain);
        vga_ifGain_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(gain));
    }

    private void setVLoop() throws Exception {
        float val = (float) vga_vloop.getValue();
        comm.getIfAmp().setVLoop(val);
        vga_vloop_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(val));
    }

    @FXML private CheckBox vga_mute;
    @FXML
    private void vgaMuteRequested() throws Exception {
        comm.getIfAmp().setMute(
                vga_mute.isSelected());
    }

    @FXML private CheckBox vga_hangOnTx;
    @FXML
    private void vgaHangOnTxRequested() throws Exception {
        comm.getIfAmp().setHangOnTransmit(
                vga_hangOnTx.isSelected());
    }

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vhth;
    @FXML
    private void vgaVhthChanged(ActionEvent event) throws Exception {
        setVhth();
    }

    private void setVhth() throws Exception {
        comm.getIfAmp().setHangThreshold(
                Float.parseFloat(
                        vga_Vhth.textProperty().getValue()));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vath_disp;

    @FXML
    private void vgaVathChanged(ActionEvent event) throws Exception {
        setVath();
    }

    private void setVath() throws Exception {
        comm.getIfAmp().setStrategyThreshold(
                Float.parseFloat(
                        vga_Vath_disp.textProperty().getValue()));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML TextField vga_Vfloor_disp;

    @FXML
    private void vgaVfloorChanged(ActionEvent event) throws Exception {
        setVfloor();
    }

    private void setVfloor() throws Exception {
        comm.getIfAmp().setNoiseFloorCompensation(
                Float.parseFloat(
                        vga_Vfloor_disp.textProperty().getValue()));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vspa;
    @FXML TextField vga_Vspa_disp;

    @FXML
    private void vgaVspaChanged(ActionEvent event) throws Exception {
        setVspa();
    }

    private void setVspa() throws Exception {
        String val = DEC_FORMAT_2DIG.format(vga_Vspa.getValue());
        vga_Vspa_disp.textProperty().setValue(val);
        comm.getIfAmp().setDecaySpeedForAttackDecayMode(Float.parseFloat(val));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vsph;
    @FXML TextField vga_Vsph_disp;

    @FXML
    private void vgaVsphChanged(ActionEvent event) throws Exception {
        setVsph();
    }

    private void setVsph() throws Exception {
        String val = DEC_FORMAT_2DIG.format(vga_Vsph.getValue());
        vga_Vsph_disp.textProperty().setValue(val);
        comm.getIfAmp().setDecaySpeedInDecayStateForHangMode(Float.parseFloat(val));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vspd;
    @FXML TextField vga_Vspd_disp;

    @FXML
    private void vgaVspdChanged(ActionEvent event) throws Exception {
        setVspd();
    }

    private void setVspd() throws Exception {
        String val = DEC_FORMAT_2DIG.format(vga_Vspd.getValue());
        vga_Vspd_disp.textProperty().setValue(val);
        comm.getIfAmp().setMaximumHangTimeInHangMode(Float.parseFloat(val));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Attack;
    @FXML TextField vga_Attack_disp;

    @FXML
    private void vgaAttackChanged(ActionEvent event) throws Exception {
        setAttack();
    }

    private void setAttack() throws Exception {
        String val = DEC_FORMAT_2DIG.format(vga_Attack.getValue());
        vga_Attack_disp.textProperty().setValue(val);
        comm.getIfAmp().setAttackTime(Float.parseFloat(val));
    }
    ///////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////
    @FXML Slider vga_Vleak;
    @FXML TextField vga_Vleak_disp;

    @FXML
    private void vgaVleakChanged(ActionEvent event) throws Exception {
        setVleak();
    }

    private void setVleak() throws Exception {
        String val = DEC_FORMAT_2DIG.format(vga_Vleak.getValue());
        vga_Vleak_disp.textProperty().setValue(val);
        comm.getIfAmp().setDecaySpeedInHangStateForHangMode(Float.parseFloat(val));
    }
    ///////////////////////////////////////////////////////

    @Override
    public void onMessage(String s) {
        int sMeter = Integer.parseInt(s);  //TODO byte/int
        s_meter.setProgress(sMeter/255.0d);
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

    @FXML private void muteLoudChanged(ActionEvent e) throws Exception {
        String command;
        switch (((RadioButton) e.getSource()).getId()) {
            case "mute_slow": command = "SLOW_SOFT_MUTE"; break;
            case "mute_fast": command = "FAST_SOFT_MUTE"; break;
            case "mute_off": command = "SOFT_MUTE_OFF"; break;
            case "loud_off": command = "LOUD_OFF"; break;
            case "loud_10": command = "LOUD_ON_10DB"; break;
            case "loud_20": command = "LOUD_ON_20DB"; break;
            default: throw new IllegalArgumentException("unknown mute loud code:" + e);
        }
        comm.getAfAmplifier().setMuteLoudness(MuteAndLoudness.valueOf(command));
    }

    boolean audioLRCoupled = true;
    String lastChannel;
    int lastLvol = 1, lastRvol = 1;

    private void setVolume(int vol, Channel channel) throws Exception {
        if (audioLRCoupled) {
            if (channel == Channel.L) { //TODO check coupler
                audio_r_vol.setValue(vol);
            } else {
                audio_l_vol.setValue(vol);
            }

            if (lastRvol != -vol && lastLvol != -vol) {
                comm.getAfAmplifier().setVolume(org.sq5nry.plaszczka.backend.api.audio.Channel.BOTH, vol);
                lastChannel = "BOTH";
                lastLvol = lastRvol = -vol;
            }

        } else {
            if (channel == Channel.L && (lastLvol != -vol)) {
                comm.getAfAmplifier().setVolume(org.sq5nry.plaszczka.backend.api.audio.Channel.LEFT, vol);
                lastChannel = "LEFT";
                lastLvol = -vol;
            }
            if (channel == Channel.R && (lastRvol != -vol)) {
                comm.getAfAmplifier().setVolume(org.sq5nry.plaszczka.backend.api.audio.Channel.RIGHT, vol);
                lastChannel = "RIGHT";
                lastRvol = -vol;
            }
        }
    }

    public void closeCommunicationChannels() {
        ifMsg.stop();
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

    /*
     * Mixer
     */
    @FXML private Slider mix_squarer;
    @FXML private Slider mix_bias;
    @FXML private Label mix_squarer_disp;
    @FXML private Label mix_bias_disp;
    @FXML private ComboBox mix_roof;

    public static final DecimalFormat DEC_FORMAT_1_2DIG = new DecimalFormat("#.00");
    private void setMixerSquarer() throws Exception {
        float fill = (float) mix_squarer.getValue();
        comm.gethModeMixer().setSquarerThreshold(fill);
        mix_squarer_disp.textProperty().setValue(DEC_FORMAT_2DIG.format(fill) + "%");
    }

    private void setMixerBias() throws Exception {
        float bias = (float) mix_bias.getValue();
        comm.gethModeMixer().setBiasPoint(bias);
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
        debounceWithBackendReq(BackendCommunicator.MIXER_ROOFING + roof);
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
        if (att != prevAtt) {
            int finalAtt = att;
            debounceWithBackendReq(BackendCommunicator.ATT + finalAtt);
        }
        prevAtt = att;
        att_disp.textProperty().setValue(att + "dB");
    }

    private void debounceWithBackendReq(String cmd) {
        Platform.runLater(() -> {
            comm.sendRequest(BackendCommunicator.AUDIO_MUTELOUD + "SLOW_SOFT_MUTE");
            comm.sendRequest(BackendCommunicator.IFAMP_MUTE + "1");
            comm.sendRequest(cmd);
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            comm.sendRequest(BackendCommunicator.IFAMP_MUTE + "0");
            comm.sendRequest(BackendCommunicator.AUDIO_MUTELOUD + "SOFT_MUTE_OFF");
        });
    }

    @FXML RadioButton bpf_20m;

    /*
     * BFP
     */
    @FXML
    private void bpfBandChanged(ActionEvent event) {
        logger.debug("bpfBandChanged: " + event);
        String band = ((RadioButton) event.getSource()).getId().substring(4);
        debounceWithBackendReq(BackendCommunicator.BAND + band);
        if (band.equals("6m")) {
            MHz = "50";
            freq_slider_khz.setMajorTickUnit(25);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(999);
        } else if (band.equals("10m")) {
            MHz = "28";
            freq_slider_khz.setMajorTickUnit(25);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(999);
        } else if (band.equals("12m")) {
            MHz = "24";
            freq_slider_khz.setMajorTickUnit(5);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(890);
            freq_slider_khz.setMax(990);
        } else if (band.equals("15m")) {
            MHz = "21";
            freq_slider_khz.setMajorTickUnit(10);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(450);
        } else if (band.equals("17m")) {
            MHz = "18";
            freq_slider_khz.setMajorTickUnit(5);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(68);
            freq_slider_khz.setMax(168);
        } else if (band.equals("20m")) {
            MHz = "14";
            freq_slider_khz.setMajorTickUnit(5);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(350);
        } else if (band.equals("30m")) {
            MHz = "10";
            freq_slider_khz.setMajorTickUnit(5);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(100);
            freq_slider_khz.setMax(150);
        } else if (band.equals("40m")) {
            MHz = "7";
            freq_slider_khz.setMajorTickUnit(10);
            freq_slider_khz.setMinorTickCount(4);
            freq_slider_khz.setMin(0);
            freq_slider_khz.setMax(200);
        } else if (band.equals("60m")) {
            MHz = "5";
            freq_slider_khz.setMajorTickUnit(30);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(353);
            freq_slider_khz.setMax(362);
        } else if (band.equals("80m")) {
            MHz = "3";
            freq_slider_khz.setMajorTickUnit(30);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(500);
            freq_slider_khz.setMax(800);
        } else if (band.equals("160m")) {
            MHz = "1";
            freq_slider_khz.setMajorTickUnit(30);
            freq_slider_khz.setMinorTickCount(5);
            freq_slider_khz.setMin(800);
            freq_slider_khz.setMax(900);
        }

        setDispFreq();
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
        String value = (String) selectivity.getValue();
        if ("None".equals(value)) {
            debounceWithBackendReq(BackendCommunicator.SELECTIVITY_BW + "0");
        } else if ("Bypass".equals(value)) {
            debounceWithBackendReq(BackendCommunicator.SELECTIVITY_BYPASS);
        } else {
            debounceWithBackendReq(BackendCommunicator.SELECTIVITY_BW + value);
        }
    }

    /*
     * Detector
     */
    @FXML ComboBox det_mode;
    @FXML ToggleButton det_ena;

    @FXML
    private void detectorStateChanged(ActionEvent event) throws Exception {
        logger.debug("detectorStateChanged: " + event);
        comm.getDetector().setEnabled(((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    private void detectorModeChanged(ActionEvent event) throws Exception {
        logger.debug("detectorModeChanged: " + event);
        setDetectorMode();
    }

    private void setDetectorMode() throws Exception {
        comm.getDetector().setRoofingFilter(Mode.valueOf(det_mode.getValue().toString()));
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
    @FXML private Rectangle box_LO;

    @FXML
    private void reinitialize(ActionEvent event) {
        Map response = comm.getReceiverCtrl().getState();
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
            } else if ("DdsUnit".equals(unitName)) {
                setUnitColor(box_LO, state);
            }
        }
        logger.debug("response=" + response.values());

        logger.debug("subscribing to s-meter");
        s_meter.setProgress(0);
        ifMsg.start();

        disableAllControls(false);
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
