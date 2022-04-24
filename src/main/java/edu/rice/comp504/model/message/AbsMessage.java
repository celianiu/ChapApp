package edu.rice.comp504.model.message;

import java.util.Date;

public abstract class AbsMessage {
    private int messageId;
    private int senderId;
    private String senderName;
    private int roomId;
    private long time;
    private String messageType;
    private String message;
    private int receiverId;
    private String receiverName;


    /**
     * contructor.
     *
     * @param messageId    messageId.
     * @param senderID     senderID.
     * @param roomId       roomId.
     * @param time         time.
     * @param messageType  messageType.
     * @param message      message.
     * @param receiverName receiver name of the message
     */
    public AbsMessage(int messageId, int senderID, String senderName, int roomId, long time, String messageType, String message, int receiverId, String receiverName) {
        this.messageId = messageId;
        this.senderName = senderName;
        this.senderId = senderID;
        this.roomId = roomId;
        this.time = time;
        this.messageType = messageType;
        this.message = message;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRoomId() {
        return roomId;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
