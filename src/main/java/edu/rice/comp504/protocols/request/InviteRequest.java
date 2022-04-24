package edu.rice.comp504.protocols.request;

public class InviteRequest extends AbsAppRequest {
    public int adminId;
    public int invitedId;
    public int roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
