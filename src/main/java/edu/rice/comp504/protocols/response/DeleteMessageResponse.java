package edu.rice.comp504.protocols.response;

public class DeleteMessageResponse extends AbsAppResponse {

    public boolean success;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
