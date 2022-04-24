package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.user.User;

public class UserLoginResponse extends AbsAppResponse {

    public User loginUser;

    @Override
    ResponseType getResponseType() {
        return ResponseType.LOGIN;
    }
}
