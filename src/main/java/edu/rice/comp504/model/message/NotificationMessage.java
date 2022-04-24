package edu.rice.comp504.model.message;

public class NotificationMessage extends AbsMessage {
    public static String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static String NOTIFICATION_INVITE = "NOTIFICATION_INVITE";
    public static String NOTIFICATION_KICK = "NOTIFICATION_KICK";
    public static String NOTIFICATION_WARN = "NOTIFICATION_WARN";
    public static int NOTIFICATION_MESSAGE_ID = -1;
    private String notificationType;

    public NotificationMessage(int messageId, int senderID, String senderName, int roomId, long time, String message, int receiverId, String receiverName) {
        super(messageId, senderID, senderName, roomId, time, NOTIFICATION_MESSAGE, message, receiverId, receiverName);
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
