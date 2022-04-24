package edu.rice.comp504.protocols.request;

public class CreateRoomRequest extends AbsAppRequest {
    public int userId;
    public int maxNumber;
    public String roomName;
    public String roomDescription;
    public boolean isPrivate;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
