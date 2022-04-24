package edu.rice.comp504.protocols.request;

public class BanRequest extends AbsAppRequest{
    public Integer banChoice;
    public Integer adminId;
    public Integer bannedId;
    public Integer roomId;

    @Override
    RequestType getRequestType() {
        return null;
    }
}
