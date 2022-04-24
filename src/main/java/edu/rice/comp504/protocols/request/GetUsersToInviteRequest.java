package edu.rice.comp504.protocols.request;

public class GetUsersToInviteRequest extends AbsAppRequest {
    public int roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
