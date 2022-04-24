package edu.rice.comp504.model.user;

import java.util.List;

public interface IRoomAttendee {
    Boolean joinChatRoom(int roomId);

    List<Integer> leaveRoom(int roomId);

    List<Integer> leaveAllRoom();

    List<Integer> getChatRoomList();


    //on new message


    //on room people changed
}
