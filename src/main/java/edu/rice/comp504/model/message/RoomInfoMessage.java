package edu.rice.comp504.model.message;

import edu.rice.comp504.model.room.RoomDB;

/**
 * 前端根据收到的RoomInfo 做对应的View处理. 每个roomID对应做什么()
 */
public class RoomInfoMessage extends AbsMessage {
    public static String ROOM_INFO_MESSAGE = "ROOM_INFO_MESSAGE";

    public enum RoomInfoType {
        KICK(0),   //谁被踢走
        BAN(1), // 有人被ban
        LEAVE(2),  //有人离开
        ENTER(3),  //有人进来
        CLOSE(4),  //断开连接
        RECALL(5), //撤回消息
        DELETE(6),
        UNBAN(7), //
        EDIT(8);
        //WARN
//BAN
        private final int value;
        RoomInfoType(int value) {
            this.value = value;
        }

    }

    //information about who leave the room, joined the room. room name changed?
    private RoomInfoType roomInfoType;

    // TODO:
    // String notificationContent : xxx left room



    public RoomInfoMessage(int messageId, int senderID, String senderName, int roomId, long time, String message, int receiverId, String receiverName, RoomInfoType roomInfoType) {
        super(messageId, senderID, senderName, roomId, time, ROOM_INFO_MESSAGE, message, receiverId, receiverName);
        this.roomInfoType = roomInfoType;
    }

}
