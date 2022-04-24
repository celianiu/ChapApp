package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.room.Room;

public class CreateRoomResponse extends AbsAppResponse {

    public Room room;

    @Override
    ResponseType getResponseType() {
        return ResponseType.NEW;
    }
}
