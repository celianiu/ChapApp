package edu.rice.comp504.protocols.request;

import java.util.List;

public class UserLoginRequest extends AbsAppRequest {

    public String username;
    public int age;
    public List<String> interests;
    public String school;

    @Override
    RequestType getRequestType() {
        return RequestType.LOGIN;
    }

    public UserLoginRequest() {

    }
}
