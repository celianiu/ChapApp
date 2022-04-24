package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.room.Room;

public class JoinRoomResponse extends AbsAppResponse {
    public Room room;
    public boolean success;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
