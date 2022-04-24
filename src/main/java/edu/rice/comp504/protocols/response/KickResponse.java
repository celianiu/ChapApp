package edu.rice.comp504.protocols.response;

public class KickResponse extends AbsAppResponse {
    public boolean success;

    @Override
    AbsAppResponse.ResponseType getResponseType() {
        return null;
    }
}
