package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.user.User;

public class ProfileResponse extends AbsAppResponse {
    public User currUser;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}