package edu.rice.comp504.model.room;

import org.eclipse.jetty.websocket.api.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomDB {
    /**
     *  RoomID : RoomObject.
     */
    private static final Map<Integer, Room> sessionRoomMap =  new ConcurrentHashMap<>();
    private static int nextRoomId = 2000002;

    /**
     * Get the session to roomName map.
     * @return The session to roomName map
     */
    public static Map<Integer,Room> getSessionRoomMap() {
        return sessionRoomMap;
    }

    /**
     * Generate the next room id.
     * @return The next room id
     */
    public static int genNextRoomId() {
        return nextRoomId++;
    }

    /**
     * Add a session room.
     * @param roomId roomId.
     * @param room  chatRoom.
     */
    public static void addSessionRoom(Integer roomId, Room room) {
        sessionRoomMap.put(roomId, room);
    }

    /**
     * Get room.
     * @param roomId roomId.
     * @return chatRoom.
     */
    public static Room getRoom(Integer roomId) {
        return sessionRoomMap.get(roomId);
    }



    /**
     * Remove room.
     * @param session The session.
     */
    public static void removeRoom(Integer roomId) {
        sessionRoomMap.remove(roomId);
    }


}
