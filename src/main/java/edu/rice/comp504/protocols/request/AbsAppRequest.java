package edu.rice.comp504.protocols.request;

public abstract class AbsAppRequest {
    enum RequestType {
        RoomInfo(0),   //房间信息改变
        MESSAGE(1),   //接受新的消息
        CREATE(2),    //创建新房间  前端要知道创建房间后的ID
        SWITCH(3),  //切换当前正在聊天的房间
        LOGIN(4), //新用户登入;
        KICK(5), //admin 踢人
        EXPLORE(6),  //获取所有房间信息
        LEAVE(7),  //离开房间
        RECALL(8),  //回收消息
        DELETE(9),  //删除消息
        INVITE(10);  //邀请他人加入房间

        private final int value;

        RequestType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private RequestType requestType;

    abstract RequestType getRequestType();
}
