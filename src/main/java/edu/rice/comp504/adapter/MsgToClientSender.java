package edu.rice.comp504.adapter;

import com.google.gson.Gson;
import edu.rice.comp504.model.message.AbsMessage;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.room.RoomDB;
import edu.rice.comp504.model.user.UserDB;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Send messages to the client.
 */
public class MsgToClientSender {
    public static Gson gson = new Gson();

    /**
     * Broadcast message to all users in the room except sender.
     * @param senderId message sender id
     * @param room one room
     * @param roomInfoMessage message content
     */
    public static void broadcastRoomInfoMessageToOthers(int senderId, Room room, AbsMessage roomInfoMessage) {
        for (Integer attendee : room.getAttendees()) {
            if (attendee != senderId) {
                try {
                    Session session = UserDB.getSessionUserMap().get(attendee);
                    if (session != null) {
                        session.getRemote().sendString(gson.toJson(roomInfoMessage));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Broadcast message to all users in the room.
     * @param room one room
     * @param roomInfoMessage message content
     */
    public static void broadcastRoomInfoMessageToAll(Room room, AbsMessage roomInfoMessage) {
        for (Integer attendee : room.getAttendees()) {
            try {
                Session session = UserDB.getSessionUserMap().get(attendee);
                if (session != null) {
                    session.getRemote().sendString(gson.toJson(roomInfoMessage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Broadcast message to all users.
     *
     * @param receiverId The message sender.
     * @param message    The message.
     */
    public static void broadcastMessage(int senderId, int receiverId, int roomId, AbsMessage message) {
        Room room = RoomDB.getRoom(roomId);
        List<Integer> roomReceivers = new ArrayList<>(room.getAttendees());
        roomReceivers.forEach(receiver -> {
            try {
                if (receiver != senderId) {
                    Session session = UserDB.getSession(receiver);
                    if (session != null) {
                        session.getRemote().sendString(gson.toJson(message));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Direct message to specific users.
     *
     * @param senderId The message sender.
     * @param message  The message.
     */
    public static void directMessage(int senderId, int receiverId, AbsMessage message) {
        Session receiverSession = UserDB.getSession(receiverId);
        try {
            if (receiverSession != null) {
                receiverSession.getRemote().sendString(gson.toJson(message));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
