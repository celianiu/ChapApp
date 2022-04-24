package edu.rice.comp504.adapter;

import edu.rice.comp504.model.message.AbsMessage;
import edu.rice.comp504.model.message.MessageFactory;
import edu.rice.comp504.model.message.NotificationMessage;
import edu.rice.comp504.model.message.RoomInfoMessage;
import edu.rice.comp504.protocols.request.*;
import edu.rice.comp504.protocols.response.*;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.room.RoomDB;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.user.UserDB;
import edu.rice.comp504.model.user.UserInRoom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static edu.rice.comp504.model.message.NotificationMessage.*;
import static edu.rice.comp504.model.message.RoomInfoMessage.ROOM_INFO_MESSAGE;

/**
 * The adapter interfaces with the frontend and the controller.
 */
public class ChatAppAdapter {

    /**
     * User Login to ChatApp.
     *
     * @param loginRequest login request information.
     * @return a UserLogin Response (a new User)
     */
    public static UserLoginResponse login(UserLoginRequest loginRequest) {
        // Build User Object
        User.Builder builder = new User.Builder(loginRequest.username)
                .age(loginRequest.age)
                .interests(loginRequest.interests)
                .school(loginRequest.school);
        User user = builder.build();

        //add session
        //UserDB.addSessionUser(user.getUserId(), null);
        //注册用户到DB;
        UserDB.addUser(user.getUserId(), user);

        UserLoginResponse response = new UserLoginResponse();
        response.loginUser = user;
        return response;
    }

    /**
     * User explore all public unjoined chatRoom that can be joined.
     *
     * @param exploreRequest HttpRequest.
     * @return a explore Response (If the recall success or not)
     */
    public static ExploreResponse explore(ExploreRequest exploreRequest) {
        int userId = exploreRequest.userId;
        ExploreResponse response = new ExploreResponse();
        List<Room> allRoomList = new ArrayList<>(RoomDB.getSessionRoomMap().values());
        List<Room> unJoinedPublicRoom = new ArrayList<>();
        for (Room r : allRoomList) {
            if (!r.getType() && !r.getAttendees().contains(userId)) {
                unJoinedPublicRoom.add(r);
            }
        }
        response.roomList = unJoinedPublicRoom;
        return response;
    }

    /**
     * User request to view his profile.
     *
     * @param profileRequest HttpRequest.
     * @return user object  (which include his profile)
     */
    public static ProfileResponse profile(ProfileRequest profileRequest) {
        int userId = profileRequest.userId;
        User user = UserDB.getUser(userId);
        ProfileResponse response = new ProfileResponse();
        response.currUser = user;
        return response;
    }

    /**
     * Create a new room in the Chatapp.
     *
     * @param createRoomRequest create room request information.
     * @return a createRoom Response (a new Room)
     */
    public static CreateRoomResponse createRoom(CreateRoomRequest createRoomRequest) {
        CreateRoomResponse response = new CreateRoomResponse();
        User user = UserDB.getUser(createRoomRequest.userId);
        Room.Builder builder = new Room.Builder(user);
        Room room = builder
                .maxNumber(createRoomRequest.maxNumber)
                .roomName(createRoomRequest.roomName)
                .isPrivate(createRoomRequest.isPrivate)
                .roomDescription(createRoomRequest.roomDescription).build();

        response.room = room;
        user.addChatRoomList(room.getRoomID());
        RoomDB.addSessionRoom(response.room.getRoomID(), response.room);
        return response;
    }

    /**
     * User join a public room.
     *
     * @param joinRoomRequest HttpRequest.
     * @return a joinRoomRequest Response (The room that the user joined)
     */
    public static JoinRoomResponse join(JoinRoomRequest joinRoomRequest) {
        Room targetroom = RoomDB.getRoom(joinRoomRequest.roomId);
        User user = UserDB.getUser(joinRoomRequest.userId);
        boolean success;
        if (!user.getGloballyBanned()) {
            success = user.joinChatRoom(joinRoomRequest.roomId);
        } else {
            success = false;
        }
        JoinRoomResponse response = new JoinRoomResponse();
        response.room = targetroom;
        response.success = success;
        return response;
    }

    /**
     * User leave a chatRoom.
     *
     * @param leaveRoomRequest HttpRequest.
     * @return a leaveRoom Response (The room that the user left)
     */
    public static LeaveRoomResponse leaveRoom(LeaveRoomRequest leaveRoomRequest) {
        UserDB.getUser(leaveRoomRequest.userId).leaveRoom(leaveRoomRequest.roomId);
        LeaveRoomResponse response = new LeaveRoomResponse();
        //todo: 不知道什么时候会失败，没有抛异常
        response.success = true;
        return response;
    }

    /**
     * User leave all joined chatroom.
     *
     * @param leaveAllRoomRequest HttpRequest.
     * @return a leaveRoom Response (The room that the user left)
     */
    public static LeaveAllRoomResponse leaveAllRoom(LeaveAllRoomRequest leaveAllRoomRequest) {
        UserDB.getUser(leaveAllRoomRequest.userId).leaveAllRoom();
        LeaveAllRoomResponse response = new LeaveAllRoomResponse();
        //todo: 不知道什么时候会失败，没有抛异常
        response.success = true;
        return response;
    }



    /**
     * User switch view of the chat room they joined.
     *
     * @param switchRoomRequest HttpRequest.
     * @return a switchRoom Response (The room that the user switched to view)
     */
    public static SwitchRoomResponse switchRoom(SwitchRoomRequest switchRoomRequest) {
        int userId = switchRoomRequest.userId;
        int roomId = switchRoomRequest.roomId;
        //todo: 是否需要根据userId 检查room在他的chatroomList里
        if (UserDB.getUser(switchRoomRequest.userId).getChatRoomList().contains(switchRoomRequest.roomId)) {
            Room room = RoomDB.getRoom(roomId);
        }
        Room room = RoomDB.getRoom(roomId);
        SwitchRoomResponse response = new SwitchRoomResponse();
        response.room = room;
        return response;
    }

    /**
     * Private room admin invite user to a private room.
     *
     * @param inviteRequest login request information.
     * @return a Invite Response (The room that the user been invited to)
     */
    public static InviteResponse invite(InviteRequest inviteRequest) {

        InviteResponse response = new InviteResponse();
        User inviter = UserDB.getUser(inviteRequest.adminId);
        User invitee = UserDB.getUser(inviteRequest.invitedId);
        Room inviteRoom = inviter.inviteUser(inviteRequest.invitedId,
                inviteRequest.roomId);
        response.room = inviteRoom;
        //建立邀请的notification
        NotificationMessage inviteMessage =
                (NotificationMessage) MessageFactory.makeFactory().makeMessage(NOTIFICATION_MESSAGE_ID,
                inviteRequest.invitedId,
                inviter.getUserName(), inviteRequest.roomId, new Date().getTime(),
                NOTIFICATION_MESSAGE, invitee.getUserName() + " are invited to room: " + inviteRoom.getRoomName(),
                invitee.getUserId(), invitee.getUserName(), null);
        inviteMessage.setNotificationType(NOTIFICATION_INVITE);
        MsgToClientSender.directMessage(inviter.getUserId(),invitee.getUserId(),inviteMessage);
        //简历邀请的room info
//        RoomInfoMessage roomInfoMessage = (RoomInfoMessage)
//                        MessageFactory.makeFactory().makeMessage(inviteRoom.genNextMessageId(),
//                        inviteRequest.invitedId,
//                        inviter.getUserName(), inviteRequest.roomId, new Date().getTime(), ROOM_INFO_MESSAGE, invitee.getUserName() + " are invited to room: " + inviteRoom.getRoomName(),
//                        invitee.getUserId(), invitee.getUserName(), null);
//
//        inviteRoom.getMessageHistory().add(roomInfoMessage);
//        //update all attendees history
//        for (Integer otherUser : inviteRoom.getAttendees()) {
//            UserInRoom receiveruser = inviteRoom.getUserByID(otherUser);
//            inviteRoom.updateUserInRoom(receiveruser, roomInfoMessage);
//        }
//        // 所有人收到 有人被邀请的通知
//        MsgToClientSender.broadcastRoomInfoMessageToAll(inviteRoom, roomInfoMessage);
        return response;
    }

    /**
     * User been banned/unbanned in a chatRoom.
     *
     * @param banRequest HttpRequest.
     * @return a banned Response (The room that the user been banned/unbanned)
     */
    public static BanResponse ban(BanRequest banRequest) {
//        int adminId = kickRequest.adminId;
//        int kickeeId = kickRequest.kickeeId;
//        int roomId = kickRequest.roomId;

//        Room room = RoomDB.getRoom(roomId);
//        kickee.leaveRoom(roomId);

        BanResponse response = new BanResponse();
        int banChoice = banRequest.banChoice;
        if (banChoice == 1) {
            response.room = UserDB.getUser(banRequest.adminId).banUser(banRequest.bannedId, banRequest.roomId);
        } else if (banChoice == 2) {
            response.room = UserDB.getUser(banRequest.adminId).unbanUser(banRequest.bannedId, banRequest.roomId);
        }
        return response;
    }

    /**
     * User been kicked a chatRoom.
     *
     * @param kickRequest HttpRequest.
     * @return a kicked Response (The room that the user been kicked out from)
     */
    public static KickResponse kick(KickRequest kickRequest) {
        KickResponse response = new KickResponse();
        Room targetRoom = RoomDB.getRoom(kickRequest.roomId);
        User kickee = UserDB.getUser(kickRequest.kickeeId);
        int count = kickee.increaseHateSpeechCount();

        targetRoom.kick(kickee.getUserId(), count);
        if (count >= 2) {
            kickee.setGloballyBanned(true);
            User user = UserDB.getUser(kickee.getUserId());
            for (int i = 0; i < user.getChatRoomList().size(); ) {
                RoomDB.getRoom(user.getChatRoomList().get(0)).kick(kickee.getUserId(), count);
            }
            user.setHateSpeechCount(0);

            response.success = true;
        } else {
            response.success = false;
        }

        return response;
    }

    /**
     * User recall a message in a chatRoom.
     *
     * @param sendMessageRequest HttpRequest.
     * @return a recallMessage Response (If the recall success or not)
     */
    public static SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
        int senderId = sendMessageRequest.senderId;
        int roomId = sendMessageRequest.roomId;
        String messageData = sendMessageRequest.messageData;
        int reveiverId = sendMessageRequest.receiverId;
        SendMessageResponse response = new SendMessageResponse();
        RoomDB.getRoom(roomId).getAttendees();
        if (RoomDB.getRoom(roomId).isBanned(senderId)) {
            response.success = false;
            return response;
        }

        AbsMessage message = RoomDB.getRoom(roomId).sendMessage(senderId, reveiverId, messageData);
        if (reveiverId == -1) {
            MsgToClientSender.broadcastMessage(senderId, reveiverId, roomId, message);
            Set<Integer> key = RoomDB.getRoom(roomId).getAttendees();
            RoomDB.getRoom(roomId).setMessageHistory(message);
            for (Integer i : key) {
                int userid = i;
                UserInRoom receiveruser = RoomDB.getRoom(roomId).getUserByID(userid);
                RoomDB.getRoom(roomId).updateUserInRoom(receiveruser, message);
            }
        } else {
            MsgToClientSender.directMessage(senderId, reveiverId, message);
            RoomDB.getRoom(roomId).setMessageHistory(message);
            UserInRoom senderuser = RoomDB.getRoom(roomId).getUserByID(senderId);
            UserInRoom receiveruser = RoomDB.getRoom(roomId).getUserByID(reveiverId);
            RoomDB.getRoom(roomId).updateUserInRoom(senderuser, message);
            RoomDB.getRoom(roomId).updateUserInRoom(receiveruser, message);
        }
        response.success = true;
        response.message = message;
        return response;
    }

    /**
     * User recall a message in a chatRoom.
     *
     * @param recallMessageRequest HttpRequest.
     * @return a recallMessage Response (If the recall success or not)
     */
    public static RecallMessageResponse recall(RecallMessageRequest recallMessageRequest) {
        int userId = recallMessageRequest.userId;
        int roomId = recallMessageRequest.roomId;
        int messageId = recallMessageRequest.messageId;
        Room room = RoomDB.getRoom(roomId);
        List<AbsMessage> messageList = room.getMessageHistory();
        messageList.removeIf(absMessage -> absMessage.getMessageId() == messageId);
        RoomDB.getRoom(roomId).deleteMessage(messageId);
        Set<Integer> key = RoomDB.getRoom(roomId).getAttendees();
        for (Integer i : key) {
            int userid = i;
            UserInRoom user = RoomDB.getRoom(roomId).getUserByID(userid);
            user.deleteMessage(messageId);
        }
        String msg = UserDB.getUser(userId).getUserName() + " has recalled a message";
        RoomInfoMessage recallMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomId, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.RECALL);
        MsgToClientSender.broadcastRoomInfoMessageToAll(RoomDB.getRoom(roomId), recallMsg);
//        room.updateUserInRoom(room.getUserByID(userId),recallMsg);
        RecallMessageResponse response = new RecallMessageResponse();
        ;
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.updateUserInRoom(receiveruser, recallMsg);
        }
        room.setMessageHistory(recallMsg);
        response.success = true;
        return response;
    }

    /**
     * Admin delete a message in a chatRoom.
     *
     * @param deleteMessageRequest HttpRequest.
     * @return a deleteMessage Response (If the recall success or not)
     */
    public static DeleteMessageResponse delete(DeleteMessageRequest deleteMessageRequest) {
        int userId = deleteMessageRequest.userId;
        int roomId = deleteMessageRequest.roomId;
        int messageId = deleteMessageRequest.messageId;
        Room room = RoomDB.getRoom(roomId);
        List<AbsMessage> messageList = room.getMessageHistory();
        messageList.removeIf(absMessage -> absMessage.getMessageId() == messageId);
        RoomDB.getRoom(roomId).deleteMessage(messageId);
        Set<Integer> key = RoomDB.getRoom(roomId).getAttendees();
        for (Integer i : key) {
            int userid = i;
            UserInRoom user = RoomDB.getRoom(roomId).getUserByID(userid);
            user.deleteMessage(messageId);
        }
        String msg = UserDB.getUser(userId).getUserName() + " has delete a message";
        RoomInfoMessage deleteMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), roomId, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.DELETE);
        MsgToClientSender.broadcastRoomInfoMessageToAll(RoomDB.getRoom(roomId), deleteMsg);
        DeleteMessageResponse response = new DeleteMessageResponse();
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.updateUserInRoom(receiveruser, deleteMsg);
        }
        room.setMessageHistory(deleteMsg);
        response.success = true;
        return response;
    }

    /**
     * User edit a message in a chatRoom.
     *
     * @param editRequest HttpRequest.
     * @return a editMessage Response (If the edit success or not) and also broadcast notification
     */
    public static EditResponse edit(EditRequest editRequest) {
        Integer userId = editRequest.userId;
        Room room = RoomDB.getRoom(editRequest.roomId);
        String changedBody = editRequest.changedBody;
        Integer changedMessageId = editRequest.messageId;
        List<AbsMessage> messageList = room.getMessageHistory();
        for (AbsMessage m : messageList) {
            if (m.getMessageId() == changedMessageId) {
                m.setMessage(changedBody);
            }
        }
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.editUserInRoomHistory(receiveruser, changedMessageId, changedBody);
        }
        String msg = UserDB.getUser(userId).getUserName() + " has edit a message";
        RoomInfoMessage editMsg = (RoomInfoMessage) MessageFactory.makeFactory().makeMessage(RoomDB.getRoom(editRequest.roomId).genNextMessageId(), userId, UserDB.getUser(userId).getUserName(), editRequest.roomId, System.currentTimeMillis(),
                RoomInfoMessage.ROOM_INFO_MESSAGE, msg, -1, "Everyone", RoomInfoMessage.RoomInfoType.DELETE);
        MsgToClientSender.broadcastRoomInfoMessageToAll(RoomDB.getRoom(editRequest.roomId), editMsg);
        for (Integer otherUser : room.getAttendees()) {
            UserInRoom receiveruser = room.getUserByID(otherUser);
            room.updateUserInRoom(receiveruser, editMsg);
        }
        room.setMessageHistory(editMsg);

        EditResponse response = new EditResponse();
        response.success = true;
        return response;
    }

    /**
     * Function for check all rooms one user has joined.
     *
     * @param joinedRoomsRequest joined request
     * @return List of room
     */
    public static JoinedRoomsResponse joinedRooms(JoinedRoomsRequest joinedRoomsRequest) {
        int userId = joinedRoomsRequest.userId;
        User user = UserDB.getUser(userId);
        UserDB.addUser(userId, user);
        List<Integer> roomIdList = user.getChatRoomList();
        List<Room> roomList = new ArrayList<>();
        for (Integer roomId : roomIdList) {
            Room room = RoomDB.getRoom(roomId);
            roomList.add(room);
        }
        JoinedRoomsResponse response = new JoinedRoomsResponse();
        response.roomList = roomList;
        return response;
    }

    /**
     * Get all information of one room.
     *
     * @param getRoomRequest get room request
     * @return Object room
     */
    public static GetRoomResponse getRoom(GetRoomRequest getRoomRequest) {
        Room room = RoomDB.getRoom(getRoomRequest.roomId);
        GetRoomResponse response = new GetRoomResponse();
        response.room = room;
        return response;
    }

    /**
     * Get all user can be invited into one room you want.
     *
     * @param getUsersToInviteRequest invite request
     * @return User list
     */
    public static GetUsersToInviteResponse getInvitableUser(GetUsersToInviteRequest getUsersToInviteRequest) {
        Room room = RoomDB.getRoom(getUsersToInviteRequest.roomId);
        GetUsersToInviteResponse response = new GetUsersToInviteResponse();
        List<User> userList = new ArrayList<>();
        List<Integer> allUser = new ArrayList<>(UserDB.getUsersKeys());
        List<Integer> userInRoom = new ArrayList<>(room.getAttendees());
        for (int u : allUser) {
            if (!userInRoom.contains(u)) {
                userList.add(UserDB.getUser(u));
            }
        }
        response.userList = userList;
        return response;
    }
}

