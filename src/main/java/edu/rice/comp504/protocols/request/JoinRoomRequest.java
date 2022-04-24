package edu.rice.comp504.protocols.request;

public class JoinRoomRequest extends AbsAppRequest {
    public int userId;
    public int roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}