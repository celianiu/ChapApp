package edu.rice.comp504.model.room;

import edu.rice.comp504.adapter.MsgToClientSender;
import edu.rice.comp504.model.message.AbsMessage;
import edu.rice.comp504.model.message.MessageFactory;
import edu.rice.comp504.model.message.RoomInfoMessage;
import edu.rice.comp504.model.user.IRoomOwner;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.user.UserDB;
import edu.rice.comp504.model.user.UserInRoom;

import java.util.*;

import static edu.rice.comp504.model.message.PrivateMessage.PRIVATE_MESSAGE;
import static edu.rice.comp504.model.message.PublicMessage.PUBLIC_MESSAGE;

public class Room {
    /**
     * UserId: UseInRoomObject.
     */
    HashMap<Integer, UserInRoom> attendees;
    IRoomOwner owner;
    private int maxNumber;
    private int roomId;
    private String roomName;
    private String roomDescription;
    private List<AbsMessage> messageHistory;
    private boolean isPrivate;
    private List<Integer> bannedList = new ArrayList<>();
    private int nextMessageId = 3000003;


    public static class Builder {
        IRoomOwner owner;
        private int maxNumber;
        private boolean isPrivate;
        private String roomDescription;
        private int roomId;
        private String roomName;
        HashMap<Integer, UserInRoom> attendees;
        private List<AbsMessage> messageHistory;

        /**
         * Constructor.
         *
         * @param owner attendee.
         */
        public Builder(User owner) {
            this.attendees = new HashMap<>();
            this.owner = owner;
            UserInRoom user = new UserInRoom(owner, this.roomId);
            this.attendees.put(owner.getUserId(), user);
            this.messageHistory = new ArrayList<>();
        }


        public Builder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }


        public Builder maxNumber(int maxNumber) {
            this.maxNumber = maxNumber;
            return this;
        }


        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder roomDescription(String roomDescription) {
            this.roomDescription = roomDescription;
            return this;
        }

        /**
         * build the room object.
         *
         * @return Room object.
         */
        public Room build() {
            Room room = new Room();
            room.roomId = RoomDB.genNextRoomId();
            room.owner = this.owner;
            room.isPrivate = this.isPrivate;
            room.attendees = this.attendees;
            room.messageHistory = this.messageHistory;
            room.maxNumber = this.maxNumber;
            room.roomName = this.roomName;
            room.roomDescription = this.roomDescription;
            return room;
        }
    }

    private Room() {

    }

    /**
     * Get next msg id.
     *
     * @return msg id
     */
    public int genNextMessageId() {
        return nextMessageId++;
    }

    public void onNewMessageReceived(String userID, String msg) {

    }

    /**
     * Send direct or broadcast message to user.
     *
     * @param senderId    userId of sender
     * @param receiverId  userId of receiver (receiverId -1 is everyOne)
     * @param messageData messageBody
     * @return AbsMessage
     */
    public AbsMessage sendMessage(int senderId, int receiverId, String messageData) {
        String senderName = UserDB.getUser(senderId).getUserName();
        if (receiverId == -1) {
            return MessageFactory.makeFactory().makeMessage(genNextMessageId(), senderId, senderName, this.roomId, System.currentTimeMillis(),
                    PUBLIC_MESSAGE, messageData, receiverId, "Everyone", null);
        } else {
            return MessageFactory.makeFactory().makeMessage(genNextMessageId(), senderId, senderName, this.roomId, System.currentTimeMillis(),
                    PRIVATE_MESSAGE, messageData, receiverId, UserDB.getUser(receiverId).getUserName(), null);
        }
    }

    public void sendDirectMessage(User user, RoomInfoMessage roomMessage) {

    }

    /**
     * Get room name.
     *
     * @return name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Set room name.
     *
     * @param roomName name
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Get room Id.
     *
     * @return roomId
     */
    public int getRoomID() {
        return roomId;
    }

    /**
     * Set the user with smallest userId to admin.
     */
    public void setAdmin() {
        if (attendees.keySet().size() != 0) {
            this.owner = UserDB.getUser(Collections.min(attendees.keySet()));
        }
    }


    /**
     * Get attendees.
     *
     * @return attendees
     */
    public Set<Integer> getAttendees() {
        return attendees.keySet();
    }

    /**
     * Get the room type (public or private).
     *
     * @return if public
     */
    public boolean getType() {
        return isPrivate;
    }

    /**
     * Get admin.
     *
     * @return owner
     */
    public IRoomOwner getAdmin() {
        return owner;
    }

    /**
     * Get max number.
     *
     * @return max
     */
    public int getMaxNumber() {
        return maxNumber;
    }

    /**
     * Get current user num.
     *
     * @return attendees' size
     */
    public int getCurrentUserNum() {
        return attendees.size();
    }

    /**
     * Get if the user is banned or not.
     *
     * @param userId userId
     * @return if success
     */
    public boolean isBanned(int userId) {
        if (bannedList.contains(userId)) {
            return true;
        }
        return false;
    }

    /**
     * Invite a user into the room.
     *
     * @param userId userId
     * @return if success
     */
    public boolean invite(int userId) {
        User user = UserDB.getUser(userId);
        user.addChatRoomList(this.getRoomID());
        UserInRoom userInRoom = new UserInRoom(user, this.roomId);
        this.attendees.put(userId, userInRoom);
        return false;
    }

    /**
     * A user join the room.
     *
     * @param userId userId
     * @return if success
     */
    public boolean join(int userId) {
        if (this.attendees == null) {
            this.attendees = new HashMap<>();
        }
        UserInRoom userInRoom = new UserInRoom(UserDB.getUser(userId), this.roomId);
        this.attendees.put(userId, userInRoom);
        return false;
    }

    /**
     * A user leave the room.
     *
     * @param userId userId
     * @return if success
     */
    public boolean leave(int userId) {
        this.attendees.remove(Integer.valueOf(userId));
        return true;
    }

    /**
     * Ban a user.
     *
     * @param userId userId
     * @return if success
     */
    public boolean ban(int userId) {
        this.bannedList.add(userId);
        return false;
    }

    /**
     * Unban a user.
     *
     * @param userId userId
     * @return if success
     */
    public boolean unban(int userId) {
        this.bannedList.remove(Integer.valueOf(userId));
        return false;
    }

    /**
     * Kick a user. If a user sends "hate" less than 2 times, the user will be warned;
     * if the user send "hate" more than 2 times, the user will be kicked off.
     *
     * @param kickeeUsedId kickeeUsedId
     * @param count        times that the user sends "hate"
     * @return if success
     */
    public boolean kick(int kickeeUsedId, int count) {
        RoomInfoMessage kickMsg;
        User kickeeUser = UserDB.getUser(kickeeUsedId);
        if (count < 2) {
            String msg = kickeeUser.getUserName() + " is been warned because of “hate” in a message";
            kickMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(genNextMessageId(), kickeeUsedId, kickeeUser.getUserName(), this.roomId, System.currentTimeMillis(),
                    RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.KICK);
            //update all attendees history
            for (Integer otherUser : this.getAttendees()) {
                UserInRoom receiveruser = this.getUserByID(otherUser);
                RoomDB.getRoom(roomId).updateUserInRoom(receiveruser, kickMsg);
            }
            MsgToClientSender.broadcastRoomInfoMessageToAll(this, kickMsg);
            this.setMessageHistory(kickMsg);
        } else {
            //踢出当前房间
            kickeeUser.removeCharRoomList(roomId);
            if (kickeeUser == this.getAdmin()) {
                this.setAdmin();
            }
            this.attendees.remove(kickeeUsedId);
            //发送踢出消息给所有
            if (this.getAttendees().size() == 0) {
                RoomDB.removeRoom(Integer.valueOf(roomId));
            }
            String msg = kickeeUser.getUserName() + " has been kicked off";
            kickMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(genNextMessageId(), kickeeUsedId, kickeeUser.getUserName(), this.roomId, System.currentTimeMillis(),
                    RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.KICK);
            //update all attendees history
            for (Integer otherUser : this.getAttendees()) {
                UserInRoom receiveruser = this.getUserByID(otherUser);
                RoomDB.getRoom(roomId).updateUserInRoom(receiveruser, kickMsg);
            }
            MsgToClientSender.broadcastRoomInfoMessageToAll(this, kickMsg);
            this.setMessageHistory(kickMsg);


        }
        return false;
    }

    /**
     * Get msg history.
     *
     * @return msg history
     */

    public List<AbsMessage> getMessageHistory() {
        return this.messageHistory;
    }

    /**
     * Add msg to msg history.
     *
     * @param message msg
     * @return added msg history
     */
    public List<AbsMessage> setMessageHistory(AbsMessage message) {
        this.messageHistory.add(message);
        return this.messageHistory;
    }

    /**
     * Find message by id.
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
     * Delete one message.
     *
     * @param messageid msg id
     * @return message list
     */
    public List<AbsMessage> deleteMessage(int messageid) {
        this.messageHistory.remove(findmessagebyid(messageid));
        return this.messageHistory;
    }

    /**
     * Get UserInRoom Object by id.
     *
     * @param id user id
     * @return UserInRoom
     */
    public UserInRoom getUserByID(int id) {
        if (this.attendees != null && this.attendees.size() > 0) {
            UserInRoom user = (UserInRoom) this.attendees.get(id);
            return user;
        }
        return null;
    }

    /**
     * Update userInRoom Object.
     *
     * @param user UserInRoom
     * @param mess AbsMessage
     * @return if success
     */
    public boolean updateUserInRoom(UserInRoom user, AbsMessage mess) {
        if (this.attendees != null && this.attendees.size() > 0) {
            UserInRoom target = this.getUserByID(user.getUser().getUserId());
            if (target == null) {
                return false;
            }
            target.messageHistory.add(mess);
            this.attendees.put(user.getUser().getUserId(), target);
            return true;
        }
        return false;
    }

    /**
     * Edit message.
     *
     * @param user        UserInRoom
     * @param messageId   MessageID of which message to edit
     * @param changedBody Replace content
     * @return if success
     */
    public boolean editUserInRoomHistory(UserInRoom user, Integer messageId, String changedBody) {
        if (this.attendees != null && this.attendees.size() > 0) {
            UserInRoom target = this.getUserByID(user.getUser().getUserId());
            if (target == null) {
                return false;
            }
            List<AbsMessage> userMessageList = target.messageHistory;
            for (AbsMessage m : userMessageList) {
                if (m.getMessageId() == messageId) {
                    m.setMessage(changedBody);
                }
            }
            this.attendees.put(user.getUser().getUserId(), target);
            return true;
        }
        return false;
    }
}

