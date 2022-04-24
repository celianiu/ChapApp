package edu.rice.comp504.protocols.request;

public class SendMessageRequest extends AbsAppRequest{

    public int senderId;
    public int roomId;
    public int receiverId;
    public String messageData;

    @Override
    RequestType getRequestType() {
        return null;
    }
}