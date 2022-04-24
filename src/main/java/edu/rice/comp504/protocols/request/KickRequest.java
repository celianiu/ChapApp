package edu.rice.comp504.protocols.request;

public class KickRequest extends AbsAppRequest{
    public Integer kickeeId;
    public Integer roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
