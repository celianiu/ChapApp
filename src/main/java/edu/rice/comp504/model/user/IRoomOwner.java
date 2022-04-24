package edu.rice.comp504.model.user;

import edu.rice.comp504.model.room.Room;

public interface IRoomOwner {

    Room inviteUser(int userID, int roomID);

    Room banUser(int userID, int roomID);

    Room unbanUser(int userID, int roomID);
}
