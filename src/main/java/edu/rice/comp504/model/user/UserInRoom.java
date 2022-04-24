package edu.rice.comp504.model.user;

import edu.rice.comp504.model.message.AbsMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Each room store this object for each attendee,
 * so that front-end can track what history message to display in each room.
 */
public class UserInRoom {
    private User user;
    private int roomId;
    private long timeJoined;
    public List<AbsMessage> messageHistory;

    /**
     * Constructor.
     */
    public UserInRoom(User user, int roomId) {
        this.user = user;
        this.roomId = roomId;
        this.timeJoined = System.currentTimeMillis();
        this.messageHistory = new ArrayList<>();
    }

    /**
     * Get the user.
     *
     * @return User
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Find msg by id.
     *
     * @param id msg id
     * @return msg
     */
    public AbsMessage findmessagebyid(int id) {
        for (AbsMessage i : messageHistory) {
            if (i.getMessageId() == id) {
                return i;
            }
        }
        return null;
    }

    /**
     * Delete msg.
     *
     * @param messageid message id
     * @return msg list
     */
    public List<AbsMessage> deleteMessage(int messageid) {
        this.messageHistory.remove(findmessagebyid(messageid));
        return this.messageHistory;
    }

    /**
     * Add msg.
     *
     * @param messageid msg id
     * @return msg list
     */
    public List<AbsMessage> addMessage(int messageid) {
        this.messageHistory.remove(findmessagebyid(messageid));
        return this.messageHistory;
    }
}
