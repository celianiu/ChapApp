package edu.rice.comp504.protocols.request;

public class EditRequest extends AbsAppRequest {
    public Integer userId;
    public Integer roomId;
    public Integer messageId;
    public String changedBody;

    @Override
    AbsAppRequest.RequestType getRequestType() {
        return null;
    }
}