package org.sq5nry.plaszczka.backend.client.communicators;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.sq5nry.plaszczka.backend.api.mgmt.ReceiverCtrl;
import org.sq5nry.plaszczka.backend.client.RequestSender;
import org.sq5nry.plaszczka.backend.common.Unit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReceiverCtrlCommunicator extends BaseCommunicator implements ReceiverCtrl {
    private static final Logger logger = Logger.getLogger(ReceiverCtrlCommunicator.class);

    public ReceiverCtrlCommunicator(RequestSender requestSender) {
        super(requestSender);
    }

    @Override
    public Map<String, Unit.State> getState() {
        return sendToReceiverCtrl(ReceiverCtrl.RESOURCE_PATH_STATE);
    }

    @Override
    public Map<String, Unit.State> initialize() {
        return sendToReceiverCtrl(ReceiverCtrl.RESOURCE_PATH_INITIALIZE);
    }

    private Map<String, Unit.State> sendToReceiverCtrl(final String path) {
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet(requestSender.getRootURL() + path);

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
        logger.debug("response: " + response == null ? "null" : response.getStatusLine());
        return new HashMap();
    }
}
