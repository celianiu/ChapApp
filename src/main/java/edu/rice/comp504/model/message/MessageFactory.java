package edu.rice.comp504.model.message;

import static edu.rice.comp504.model.message.NotificationMessage.NOTIFICATION_MESSAGE;
import static edu.rice.comp504.model.message.PrivateMessage.PRIVATE_MESSAGE;
import static edu.rice.comp504.model.message.PublicMessage.PUBLIC_MESSAGE;
import static edu.rice.comp504.model.message.RoomInfoMessage.ROOM_INFO_MESSAGE;

public class MessageFactory {
    public static MessageFactory ONLYMsgFAC;

    /**
     * Only makes 1 message factory.
     *
     * @return The message factory
     */
    public static MessageFactory makeFactory() {
        if (ONLYMsgFAC == null) {
            ONLYMsgFAC = new MessageFactory();
        }
        return ONLYMsgFAC;
    }

    /**
     * Make object of message.
     *
     * @return The message object
     */
    public AbsMessage makeMessage(int messageId, int senderID, String senderName, int roomId, long time, String messageType, String message, int receiverId, String receiverName, RoomInfoMessage.RoomInfoType roomInfoType) {
        if (messageType == PUBLIC_MESSAGE) {
            return new PublicMessage(messageId, senderID, senderName, roomId, time, message, receiverId, receiverName);
        } else if (messageType == PRIVATE_MESSAGE) {
            return new PrivateMessage(messageId, senderID, senderName, roomId, time, message, receiverId, receiverName);
        } else if (messageType == ROOM_INFO_MESSAGE) {
            return new RoomInfoMessage(messageId, senderID, senderName, roomId, time, message, receiverId, receiverName, roomInfoType);
        } else {
            return new NotificationMessage(messageId, senderID, senderName, roomId, time, message, receiverId, receiverName);
        }

    }
}
