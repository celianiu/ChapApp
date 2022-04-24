package edu.rice.comp504.model.message;

import edu.rice.comp504.model.user.UserDB;

import java.util.Date;

public class PublicMessage extends AbsMessage {
    public String senderUsername;
    //todo: type display recall display
    /**
     * the message in a specific room.
     */
    public static final String PUBLIC_MESSAGE = "PUBLIC_MESSAGE";

    public PublicMessage(int messageId, int senderID,String senderName, int roomId, long time, String message,int receiverId,String receiverName) {
        super(messageId, senderID, senderName,roomId, time, PUBLIC_MESSAGE, message,receiverId,receiverName);
        senderUsername = UserDB.getUser(senderID).getUserName();
    }
}
