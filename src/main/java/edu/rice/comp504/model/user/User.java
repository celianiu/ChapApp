package edu.rice.comp504.model.user;

import edu.rice.comp504.adapter.MsgToClientSender;
import edu.rice.comp504.model.message.MessageFactory;
import edu.rice.comp504.model.message.RoomInfoMessage;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.room.RoomDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.rice.comp504.model.message.RoomInfoMessage.ROOM_INFO_MESSAGE;

public class User implements IRoomOwner, IRoomAttendee {

    private Integer userId;
    private String userName;
    private int age;
    private int hateSpeechCount = 0;
    private String school;
    private List<String> interests;
    private List<Integer> chatRoomList;
    private boolean isGloballyBanned;

    private User() {

    }

    public static class Builder {
        private int userId;
        private String userName;
        private int age;
        private String school;
        private List<String> interests;
        private Boolean isGloballyBanned;
        private List<Integer> chatRoomList;

        /**
         * the constructor of builder.
         *
         * @param userName userName.
         */
        public Builder(String userName) {
            this.userName = userName;
            this.userId = UserDB.genNextUserId();
            this.isGloballyBanned = false;
            this.chatRoomList = new ArrayList<>();
        }

        public User.Builder age(int age) {
            this.age = age;
            return this;
        }

        public User.Builder school(String school) {
            this.school = school;
            return this;
        }

        public User.Builder interests(List<String> interests) {
            this.interests = interests;
            return this;
        }

        /**
         * build a user object.
         *
         * @return a user object.
         */
        public User build() {
            User user = new User();
            user.userName = userName;
            user.userId = userId;
            user.age = age;
            user.school = school;
            user.interests = interests;
            user.isGloballyBanned = isGloballyBanned;
            user.chatRoomList = chatRoomList;

            return user;
        }
    }

    /**
     * Join chat room based on room id.
     *
     * @param roomId roomId
     * @return if success
     */
    @Override
    public Boolean joinChatRoom(int roomId) {
        // if the room is not private room
        Room room = RoomDB.getRoom(roomId);
        if (!room.getType() && room.getCurrentUserNum() < room.getMaxNumber()) {
            this.chatRoomList.add(roomId);
            room.join(this.userId);
            String msg = UserDB.getUser(userId).getUserName() + " has joined the room";
            RoomInfoMessage joinMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomId, System.currentTimeMillis(),
                    RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.ENTER);
            MsgToClientSender.broadcastRoomInfoMessageToOthers(userId, RoomDB.getRoom(roomId), joinMsg);
            for (Integer otherUser : room.getAttendees()) {
                UserInRoom receiveruser = room.getUserByID(otherUser);
                room.updateUserInRoom(receiveruser, joinMsg);
            }
            room.setMessageHistory(joinMsg);
            return true;
        }
        return false;
    }


    /**
     * Leave a room.
     *
     * @param roomId room id
     * @return rooms which the user already joined
     */
    @Override
    public List<Integer> leaveRoom(int roomId) {
        Room targetRoom = RoomDB.getRoom(roomId);
        targetRoom.leave(userId);
        if (this == targetRoom.getAdmin()) {
            targetRoom.setAdmin();
        }


        this.chatRoomList.remove(Integer.valueOf(roomId));
        String msg = UserDB.getUser(userId).getUserName() + " has left the room";
        RoomInfoMessage leaveMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomId, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.LEAVE);
        MsgToClientSender.broadcastRoomInfoMessageToOthers(userId, RoomDB.getRoom(roomId), leaveMsg);
        if (targetRoom.getAttendees().size() == 0) {
            RoomDB.removeRoom(Integer.valueOf(roomId));
        }
        for (Integer otherUser : targetRoom.getAttendees()) {
            UserInRoom receiveruser = targetRoom.getUserByID(otherUser);
            targetRoom.updateUserInRoom(receiveruser, leaveMsg);
        }
        targetRoom.setMessageHistory(leaveMsg);
        return this.chatRoomList;

    }

    /**
     * Leave all room.
     *
     * @return rooms which the user already left
     */
    @Override
    public List<Integer> leaveAllRoom() {
        for (Integer roomId : this.chatRoomList) {
            Room targetRoom = RoomDB.getRoom(roomId);
            if (this == targetRoom.getAdmin()) {
                targetRoom.setAdmin();
            }
            targetRoom.leave(userId);
            String msg = UserDB.getUser(userId).getUserName() + " has left the room";
            RoomInfoMessage leaveMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomId, System.currentTimeMillis(),
                    RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.LEAVE);
            MsgToClientSender.broadcastRoomInfoMessageToOthers(userId, RoomDB.getRoom(roomId), leaveMsg);
            if (targetRoom.getAttendees().size() == 0) {
                RoomDB.removeRoom(Integer.valueOf(roomId));
            }
            for (Integer otherUser : targetRoom.getAttendees()) {
                UserInRoom receiveruser = targetRoom.getUserByID(otherUser);
                targetRoom.updateUserInRoom(receiveruser, leaveMsg);
            }
            targetRoom.setMessageHistory(leaveMsg);

        }
        this.chatRoomList.clear();
        return this.chatRoomList;


    }

    /**
     * Invite a user who is not in the room.
     *
     * @param userID userID
     * @param roomID roomID
     * @return the target room
     */
    @Override
    public Room inviteUser(int userID, int roomID) {
        Room inviteRoom = RoomDB.getRoom(roomID);
        User invitee = UserDB.getUser(userID);
        if (RoomDB.getRoom(roomID).getCurrentUserNum() < RoomDB.getRoom(roomID).getMaxNumber()){
            if (this == RoomDB.getRoom(roomID).getAdmin()) {
                RoomDB.getRoom(roomID).invite(userID);
                //简历邀请的room info
                RoomInfoMessage roomInfoMessage = (RoomInfoMessage)
                        MessageFactory.makeFactory().makeMessage(inviteRoom.genNextMessageId(),
                                userID,
                                invitee.getUserName(), roomID, new Date().getTime(), ROOM_INFO_MESSAGE, invitee.getUserName() + " are invited to room: " + inviteRoom.getRoomName(),
                                invitee.getUserId(), invitee.getUserName(), null);

                inviteRoom.getMessageHistory().add(roomInfoMessage);
                //update all attendees history
                for (Integer otherUser : inviteRoom.getAttendees()) {
                    UserInRoom receiveruser = inviteRoom.getUserByID(otherUser);
                    inviteRoom.updateUserInRoom(receiveruser, roomInfoMessage);
                }
                // 所有人收到 有人被邀请的通知
                MsgToClientSender.broadcastRoomInfoMessageToAll(inviteRoom, roomInfoMessage);
            }
        }
        return RoomDB.getRoom(roomID);
    }

    /**
     * Ban a user.
     *
     * @param userID user who is banned
     * @param roomID room id where the user is
     * @return the Room
     */
    @Override
    public Room banUser(int userID, int roomID) {
        if (this == RoomDB.getRoom(roomID).getAdmin()) {
            RoomDB.getRoom(roomID).ban(userID);
        }
        User bannedUser = UserDB.getUser(userID);
        String msg = bannedUser.getUserName() + " has been banned";
        RoomInfoMessage banMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomID).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomID, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.BAN);
        MsgToClientSender.broadcastRoomInfoMessageToAll(RoomDB.getRoom(roomID), banMsg);
        Room room = RoomDB.getRoom(roomID);
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.updateUserInRoom(receiveruser, banMsg);
        }
        room.setMessageHistory(banMsg);
        return RoomDB.getRoom(roomID);
    }

    /**
     * Unban a user.
     *
     * @param userID user who is banned
     * @param roomID room id where the user is
     * @return the Room
     */
    @Override
    public Room unbanUser(int userID, int roomID) {
        if (this == RoomDB.getRoom(roomID).getAdmin()) {
            RoomDB.getRoom(roomID).unban(userID);
        }
        String msg = UserDB.getUser(userID).getUserName() + " has been removed from block list";
        RoomInfoMessage unBanMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomID).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomID, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.UNBAN);
        MsgToClientSender.broadcastRoomInfoMessageToAll(RoomDB.getRoom(roomID), unBanMsg);
        Room room = RoomDB.getRoom(roomID);
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.updateUserInRoom(receiveruser, unBanMsg);
        }
        room.setMessageHistory(unBanMsg);
        return RoomDB.getRoom(roomID);
    }

    /**
     * Get all rooms the user joined.
     *
     * @return all rooms the user joined
     */
    @Override
    public List<Integer> getChatRoomList() {
        return this.chatRoomList;
    }

    /**
     * Add one room in the room list.
     *
     * @param charRoom roomId
     */
    public void addChatRoomList(int charRoom) {
        this.chatRoomList.add(charRoom);
    }

    /**
     * Remove one room in the room list.
     *
     * @param charRoom roomId
     */
    public void removeCharRoomList(int charRoom) {
        this.chatRoomList.remove(Integer.valueOf(charRoom));
    }

    /**
     * Get user id.
     *
     * @return userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Get user name.
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get if the user is globally banned.
     *
     * @return status
     */
    public boolean getGloballyBanned() {
        return isGloballyBanned;
    }

    public void setGloballyBanned(boolean globallyBanned) {
        isGloballyBanned = globallyBanned;
    }

    /**
     * Get hateSpeechCount.
     *
     * @return count
     */
    public int getHateSpeechCount() {
        return hateSpeechCount;
    }

    /**
     * Set hateSpeechCount.
     *
     * @param hateSpeechCount count
     */
    public void setHateSpeechCount(int hateSpeechCount) {
        this.hateSpeechCount = hateSpeechCount;
    }

    /**
     * Increase "hate" message count.
     *
     * @return count
     */
    public int increaseHateSpeechCount() {
        return ++this.hateSpeechCount;
    }
}