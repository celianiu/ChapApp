package edu.rice.comp504.protocols.response;


import edu.rice.comp504.model.user.User;

import java.util.List;

public class GetUsersToInviteResponse extends AbsAppResponse {
    public List<User> userList;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
