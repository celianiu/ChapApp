package edu.rice.comp504.model.message;

import java.util.Date;

public class PrivateMessage extends AbsMessage {

    int targetUserID;
    public static final String PRIVATE_MESSAGE = "PRIVATE_MESSAGE";

    public PrivateMessage(int messageId, int senderID, String senderName, int roomId, long time, String message, int receiverId, String receiverName) {
        super(messageId, senderID, senderName, roomId, time, PRIVATE_MESSAGE, message, receiverId, receiverName);

    }
}
