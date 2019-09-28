package org.sq5nry.plaszczka.backend.client.communicators;

import org.sq5nry.plaszczka.backend.api.display.FrequencyDisplay;
import org.sq5nry.plaszczka.backend.client.RequestSender;

import java.io.IOException;

public class FrequencyDisplayCommunicator extends BaseCommunicator implements FrequencyDisplay {
    public FrequencyDisplayCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public void setFrequency(int freq) {
        requestSender.sendRequest(FrequencyDisplayCommunicator.RESOURCE_PATH_SET_FREQUENCY.replace("{freq}", Float.toString(freq)));
    }

    @Override
    public void setMarker(int position) {
        requestSender.sendRequest(FrequencyDisplayCommunicator.RESOURCE_PATH_SET_MARKER.replace("{position}", Integer.toString(position)));
    }

    @Override
    public void setBlankLeadingZeroes(boolean blankLeadingZeroes) {
        requestSender.sendRequest(FrequencyDisplayCommunicator.RESOURCE_PATH_SET_BLANK_LEADING_ZEROES.replace("{enabled}", Boolean.toString(blankLeadingZeroes)));
    }
}
