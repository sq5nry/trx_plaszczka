package org.sq5nry.plaszczka.backend.api.vga;

/**
 * Set-point Reference
 */
public interface IfAmp {
    /**
     * Vsph sets the decay speed during the DECAY state for the AGC in Hang-Mode.
     * Usually a relatively fast decay speed is used. With the speed specified by Vsph,
     * the Vslow control voltage is decayed until it equals Vfast again and the AGC re-enters HANG state.
     *
     * @param speed 1..255
     *  Reference Voltage: 	5.0V
     *  Minimum Value: 	1 	    19.6mV, 6.06dB/s, 21.6s/131dB
     *  Maximum Value: 	255 	5.0V,   1546dB/s, 84.8ms/131dB
     *  Step Size: 	            19.6mV 	6.06dB/s
     *  Recommended Default Value: 	32 	194dB/s, 0.675s/131dB
     */
    void setDecaySpeedInDecayStateForHangMode(int speed);

    /**
     * Vspa sets the decay speed for the AGC in Attack/Decay-Mode.
     * Usually a relatively slow decay speed is used for quiet operation of the AGC. With the speed specified by Vspa,
     * the Vslow control voltage is decayed until it equals Vfast again.
     *
     * @param speed 1..255
     *  Reference Voltage: 	5.0V
     *  Minimum Value: 	    1 	    19.6mV, 6.06dB/s, 21.6s/131dB
     *  Maximum Value: 	    255 	5.0V, 1546dB/s, 84.8ms/131dB
     *  Step Size: 	                19.6mV 	6.06dB/s
     *  Recommended Default Value: 	8 	48.5dB/s, 2.7s/131dB
     */
    void setDecaySpeedForAttackDecayMode(int speed);

    /**
     * Vleak sets the decay speed during the HANG state in Hang-Mode. The speed can be 0 but usually a little
     * amount of leaking is configured to have the AGC follow slow QSB during HANG state in Hang-Mode.
     * A voltage divider with buffer is used to provide a more fine grained and smaller output value.
     *
     * @param speed 0..255
     *  Reference Voltage: 	5.0V
     *  Minimum Value: 	    0 	    0V, 0dB/s
     *  Maximum Value: 	    255 	0.455V, 140.7dB/s
     *  Step Size: 	                1.78mV 	0.551dB/s
     *  Recommended Default Value: 	4 	7.14mV, 2.21dB/s
     */
    void setDecaySpeedInHangStateForHangMode(int speed);

    /**
     * Vfloor sets the compensation for the noise floor of the diode detector. Vfloor is subtracted from the output
     * of the diode detector in order to obtain a Vagc control voltage that starts close to 0V at the beginning
     * of the control range. A voltage divider with buffer is used to provide a more fine grained and smaller
     * output value. In practice Vagc will start at plus minus 300mV for the given default value.
     *
     * Rather than to adjust Vfloor to have Vagc start at 300mV in closed-loop, Vfloor should be used to precisely
     * set the end of the control range. The exact end of the control range is set by Vfloor to correspond
     * with the strongest input signal that the signal path can handle.
     *
     * @param val 0..255
     *  Reference Voltage: 	5.0V
     *  Minimum Value: 	    0 	    0V
     *  Maximum Value: 	    255 	0.763V
     *  Step Size: 	                2.99mV 	0.1dB
     *  Recommended Default Value: 	151 	0.452V, 15.1dB
     */
    void setNoiseFloorCompensation(int val);

    /**
     * Vath sets the threshold value used for automatically choosing the AGC strategy.
     * Signals with Vslow below Vath will use Attack/Decay-Mode. When Vslow is above Vath, Hang-Mode will be used.
     * Given the default value (112) input signals below S9 will use Attack/Decay-Mode and above S9 Hang-Mode.
     *
     * @param val 1..255
     *  Reference Voltage: 	3.894V
     *  Minimum Value: 	    0 	    0V, -131dBm
     *  Maximum Value: 	    255 	3.894V, 0dBm
     *  Step Size: 	                15.3mV 	0.515dB
     *  Recommended Default Value: 	112 	1.71V, -73.3dBm, S9
     */
    void setStrategyThreshold(int val);

    /**
     * Vhth represents the hang threshold value used to set the direction of the hang integrator.
     * If Vfast + Vhth < Vslow, then the integrator moves towards the DECAY state, otherwise it moves towards HANG state.
     * In other words: Vhth defines the window in dB that the Hang AGC allows Vfast to decrease during HANG state
     * to maintain its preference for HANG state.
     *
     * @param val 1..255
     *  Reference Voltage: 	3.894V
     *  Minimum Value: 	    1 	    15.3mV, 0.515dB
     *  Maximum Value: 	    255 	3.894V, 131dB
     *  Step Size: 	                15.3mV 	0.515dB
     *  Recommended Default Value: 	20 	0.305V, 10.3dB
     */
    void setHangThreshold(int val);

    /**
     * Vloop has a dual function:
     *     For small values (< 80), it determines the voltage that is subtracted from Vfast to produce Vfast2.
     *     Usually a value that represents 6dB is used in order to minimize the gain modulated IMD produced
     *     in a closed loop AGC. This value depends on the loop bandwidth provided by the attack/decay times
     *     of the FAST path. In this mode it is an analog output.
     *
     *     For large values, Vloop is used to mute the AD602 amplifier that provides signal to the diode detector.
     *     In this mode it is a digital output. To open the AGC loop for manual gain control mode use 255.
     *
     * @param val 0..255
     *  Reference Voltage: 	3.894V
     *  Minimum Value: 	    0 	    0V, 0dB
     *  Maximum Value: 	    255 	3.894V, 131dB
     *  Step Size: 	                15.3mV 	0.515dB
     *  Recommended Default Value: 	12 	0.184V, 6.2dB
     */
    void setVLoop(int val);

    /**
     * Vgain is used to set the maximum allowed gain of the signal path. This control is usually called IF-gain
     * or RF-gain in most receivers. When the loop is opened by means of Vloop, Vgain provides pure manual gain control.
     *
     * @param gain 0..255
     *  Reference Voltage: 	3.894V
     *  Minimum Value: 	    0 	    0V, 109.6dB gain
     *  Maximum Value: 	    255 	3.894V, -21.5dB gain
     *  Step Size: 	                15.3mV 	0.515dB
     *  Recommended Default Value: 	0 	0V, 109.6dB gain
     */
    void setMaximumGain(int gain);


    /**
     * Vspd sets the maximum hang time of the AGC in Hang-Mode.
     * This is the interval starting when the hang integrator begins to integrate towards the DECAY phase until
     * DECAY phase is actually reached. The 8-bit 100KΩ digital pot-meter is used to create 256 hang integrator
     * speed steps from 0V to 5V.
     *
     * @param val 1..255
     *  Reference Voltage: 	5.0
     *  Minimum Value: 	    1 	    15.3mV, 31s
     *  Maximum Value: 	    255 	5V, 122ms
     *  Step Size: 	                15.3mV
     *  Recommended Default Value: 	15 	0V, 2.0s
     */
    void setMaximumHangTimeInHangMode(int val);

    /**
     * Attack sets the attack time of the AGC in both Attack/Decay-Mode and Hang-Mode.
     * This is implemented directly with the 8-bit digital pot-meter used as a variable resistor in an RC-time constant.
     *
     * @param val 0..255
     *  Minimum Value: 	0 	    0Ω, 73ms
     *  Maximum Value: 	255 	100KΩ, 400ms
     *  Step Size: 	            392Ω 	1.3ms
     *  Recommended Default Value: 	10 	0V, 85ms
     */
    void setAttackTime(int val);

    /**
     * Vhot is the "hang on transmit" bit. If set, the hang integrator is forced deep in to HANG state quickly.
     * This bit is most relevant if the receiver is used in combination with a transmitter to maintain
     * the receiver's AGC level during short transmissions.
     *
     * @param enabled HOT enabled
     */
    void setHangOnTransmit(boolean enabled);

    /**
     * Vmute is used to completely mute the signal path. If set, all AD600's in the signal path are disabled.
     * This bit is most relevant if the receiver is used in combination with a transmitter in half duplex mode
     * to mute the IF during transmission.
     *
     * @param enabled Signal Path Muted
     */
    void setMute(boolean enabled);
}
