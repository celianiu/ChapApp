package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.message.AbsMessage;

public class SendMessageResponse extends AbsAppResponse {
    public boolean success;
    public AbsMessage message;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
