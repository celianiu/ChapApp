package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.room.Room;

import java.util.List;

public class ExploreResponse extends AbsAppResponse {
    public List<Room> roomList;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
