package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.room.Room;

public class GetRoomResponse extends AbsAppResponse {

    public Room room;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
