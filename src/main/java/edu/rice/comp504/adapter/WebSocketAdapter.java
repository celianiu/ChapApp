package edu.rice.comp504.adapter;

import edu.rice.comp504.model.message.AbsMessage;
import edu.rice.comp504.model.message.MessageFactory;
import edu.rice.comp504.model.message.RoomInfoMessage;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.room.RoomDB;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.user.UserDB;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.rice.comp504.model.message.RoomInfoMessage.ROOM_INFO_MESSAGE;

/**
 * Processing of web socket.
 */
@WebSocket
public class WebSocketAdapter {
    ChatAppAdapter controller;

    /**
     * Open user's session.
     *
     * @param session The user whose session is opened.
     */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        List<String> userIdList = session.getUpgradeRequest().getParameterMap().get("userId");
        if (userIdList.size() == 1) {
            int userId = Integer.parseInt(userIdList.get(0));
            //注册session
            UserDB.getSessionUserMap().put(userId, session);
        }
    }


    /**
     * Close the user's session.
     *
     * @param session The user whose session is closed.
     */
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        List<String> userIdList = session.getUpgradeRequest().getParameterMap().get("userId");
        if (userIdList.size() == 1) {
            int userId = Integer.parseInt(userIdList.get(0));
            User user = UserDB.getUser(userId);
            //解除session 删除用户
            if (UserDB.getSessionUserMap().containsKey(userId)) {
                UserDB.getSessionUserMap().remove(userId);
            }

            //发送 离开房间消息 给 房间所有人
            String msg = "user " + user.getUserName() + " leaves because of connection close";
            for (Integer roomId : user.getChatRoomList()) {
                Room joinedRoom = RoomDB.getRoom(roomId);
                int newMessageId = joinedRoom.genNextMessageId();
                AbsMessage leaveRoom = MessageFactory.makeFactory().makeMessage(newMessageId, userId, user.getUserName(), roomId, new Date().getTime(), ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.CLOSE);
                joinedRoom.setMessageHistory(leaveRoom);
                //user.leaveRoom(roomId);
                MsgToClientSender.broadcastRoomInfoMessageToOthers(userId, joinedRoom, leaveRoom);
                List<Integer> allUserInRoom = new ArrayList<>(joinedRoom.getAttendees());
                for (int u : allUserInRoom) {
                    joinedRoom.updateUserInRoom(joinedRoom.getUserByID(u), leaveRoom);
                }
            }
            user.leaveAllRoom();
        }
    }

    /**
     * Send a message.
     *
     * @param session The session user sending the message.
     * @param message The message to be sent.
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        // TODO: broadcast the message to all clients
        //Room.broadcastMessage(UserDB.getUser(session),message);
    }
}
