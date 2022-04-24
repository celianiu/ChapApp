package edu.rice.comp504.protocols.request;

public class DeleteMessageRequest extends AbsAppRequest {

    public Integer userId;
    public Integer roomId;
    public Integer messageId;


    @Override
    RequestType getRequestType() {
        return null;
    }
}
