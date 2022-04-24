package edu.rice.comp504.model.user;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserDB {
    /**
     * UserId: WebSocketSession.
     */
    private static final Map<Integer, Session> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * UserId : user object.
     */
    private static final Map<Integer, User> allUsers = new ConcurrentHashMap<>();

    private static int nextUserId = 1000001;

    /**
     * Constructor.
     */
    public UserDB() {
    }

    /**
     * Get the session to username map.
     *
     * @return The session to username map
     */
    public static Map<Integer, Session> getSessionUserMap() {
        return sessionUserMap;
    }

    /**
     * Generate the next user id.
     *
     * @return The next user id
     */
    public static int genNextUserId() {
        return nextUserId++;
    }

    /**
     * Add a session user.
     *
     * @param session The session.
     * @param userId  The username.
     */
    public static void addSessionUser(Integer userId, Session session) {
        sessionUserMap.put(userId, session);
    }

    /**
     * Add a session user.
     *
     * @param user The user object.
     * @param userId  The username.
     */
    public static void addUser(Integer userId, User user) {
        allUsers.put(userId, user);
    }


    /**
     * Get users key.
     *
     * @return All users key.
     */
    public static Set<Integer> getUsersKeys() {
        return allUsers.keySet();
    }

    public static Map<Integer, User> getAllUsers() {
        return allUsers;
    }

    /**
     * Remove user.
     *
     * @param userId The userId.
     */
    public static User getUser(int userId) {
        return allUsers.get(userId);
    }

    /**
     * Remove user.
     *
     * @param userId The user.
     */
    public static void removeUser(int userId) {
        sessionUserMap.remove(userId);
        allUsers.remove(userId);
    }

    /**
     * Get open sessions.
     *
     * @param userId The userId.
     * @return The session
     */
    public static Session getSession(int userId) {
        return sessionUserMap.get(userId);
        //return sessionUserMap.keySet();
    }

}
