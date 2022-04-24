package edu.rice.comp504.protocols.request;

public class LeaveAllRoomRequest extends AbsAppRequest {
    public Integer userId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}