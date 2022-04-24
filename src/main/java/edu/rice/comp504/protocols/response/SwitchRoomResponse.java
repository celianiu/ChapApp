package edu.rice.comp504.protocols.response;

import edu.rice.comp504.model.room.Room;

public class SwitchRoomResponse extends AbsAppResponse {
    public Room room;

    @Override
    ResponseType getResponseType() {
        return null;
    }
}
