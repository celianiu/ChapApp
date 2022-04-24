package edu.rice.comp504.protocols.request;

public class GetRoomRequest extends AbsAppRequest {
    public Integer userId;
    public Integer roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
