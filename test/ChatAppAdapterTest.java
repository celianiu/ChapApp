import edu.rice.comp504.adapter.ChatAppAdapter;
import edu.rice.comp504.model.message.AbsMessage;
import edu.rice.comp504.model.user.UserInRoom;
import edu.rice.comp504.protocols.request.*;
import edu.rice.comp504.protocols.response.*;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.room.RoomDB;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.user.UserDB;
import org.junit.After;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ChatAppAdapterTest extends junit.framework.TestCase {
    @Test
    public void getUsers() {
        UserDB.getSessionUserMap().clear();
        UserDB.getAllUsers().clear();
        Assert.assertEquals(UserDB.getAllUsers().size(),0);
        User testadmin = new User.Builder("testadmin").build();
        User testuser = new User.Builder("testuser").build();
        User testuser2 = new User.Builder("testuser2").build();
        UserDB.addUser(testadmin.getUserId(), testadmin);
        UserDB.addUser(testuser.getUserId(), testuser);
        UserDB.addUser(testuser2.getUserId(), testuser2);
        Room.Builder builder = new Room.Builder(testadmin);
        Room room1 = builder
                .maxNumber(2)
                .roomName("testroom1")
                .isPrivate(false)
                .roomDescription("test").build();
        testadmin.addChatRoomList(room1.getRoomID());
        Room.Builder builder2 = new Room.Builder(testadmin);
        Room room2 = builder2
                .maxNumber(2)
                .roomName("testroom2")
                .isPrivate(false)
                .roomDescription("test2").build();
        testadmin.addChatRoomList(room2.getRoomID());
        Room.Builder builder3 = new Room.Builder(testadmin);
        Room room3 = builder3
                .maxNumber(3)
                .roomName("testroom3")
                .isPrivate(false)
                .roomDescription("test3").build();
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        RoomDB.addSessionRoom(room2.getRoomID(), room2);
        RoomDB.addSessionRoom(room3.getRoomID(), room3);
        testadmin.addChatRoomList(room3.getRoomID());
        JoinRoomRequest testjoin1 = new JoinRoomRequest();
        testjoin1.roomId = room2.getRoomID();
        testjoin1.userId = testuser.getUserId();
        ChatAppAdapter.join(testjoin1);
        JoinRoomRequest testjoin2 = new JoinRoomRequest();
        testjoin2.roomId = room3.getRoomID();
        testjoin2.userId = testuser.getUserId();
        ChatAppAdapter.join(testjoin2);
        JoinRoomRequest testjoin3 = new JoinRoomRequest();
        testjoin3.roomId = room3.getRoomID();
        testjoin3.userId = testuser2.getUserId();
        ChatAppAdapter.join(testjoin3);
        List<User> oneuser = new ArrayList<>();
        oneuser.add(testuser2);
        oneuser.add(testuser);
        List<User> twouser = new ArrayList<>();
        twouser.add(testuser2);
        List<User> threeuser = new ArrayList<>();
        GetUsersToInviteRequest getuser1 = new GetUsersToInviteRequest();
        getuser1.roomId = room1.getRoomID();
        GetUsersToInviteRequest getuser2 = new GetUsersToInviteRequest();
        getuser2.roomId = room2.getRoomID();
        GetUsersToInviteRequest getuser3 = new GetUsersToInviteRequest();
        getuser3.roomId = room3.getRoomID();
        UserDB.getSessionUserMap().clear();
        assertEquals("test for getuser of one user room", oneuser, ChatAppAdapter.getInvitableUser(getuser1).userList);
        assertEquals("test for getuser of two user room", twouser, ChatAppAdapter.getInvitableUser(getuser2).userList);
        assertEquals("test for getuser of three user room", threeuser, ChatAppAdapter.getInvitableUser(getuser3).userList);
    }

    @Test
    public void edit() {
        User.Builder builder = new User.Builder("testadmin");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        Room.Builder roombuilder = new Room.Builder(user);
        Room room = roombuilder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        SendMessageRequest request = new SendMessageRequest();
        request.senderId = user.getUserId();
        request.receiverId = -1;
        request.roomId = room.getRoomID();
        request.messageData = "hello test";
        ChatAppAdapter.sendMessage(request);
        EditRequest testedit = new EditRequest();
        testedit.roomId = room.getRoomID();
        testedit.userId = user.getUserId();
        testedit.messageId = 3000003;
        testedit.changedBody = "hello edit";
        ChatAppAdapter.edit(testedit);
        UserInRoom testuser = RoomDB.getRoom(room.getRoomID()).getUserByID(user.getUserId());
        assertEquals("edit test for userhistory", "hello edit",testuser.messageHistory.get(0).getMessage());
    }

    @Test
    public void leaveAllRooms() {
        // create a user
        User admin = new User.Builder("testuser").build();
        UserDB.addUser(admin.getUserId(), admin);
        // create two rooms
        Room.Builder builder = new Room.Builder(admin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        Room room1 = new Room.Builder(admin)
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        // create a user2
        User user = new User.Builder("user").build();
        UserDB.addUser(user.getUserId(), user);
        // let user2 join room
        user.joinChatRoom(room.getRoomID());
        user.joinChatRoom(room1.getRoomID());
        LeaveAllRoomRequest request = new LeaveAllRoomRequest();
        request.userId = user.getUserId();
        LeaveAllRoomResponse response = ChatAppAdapter.leaveAllRoom(request);
        assertFalse(room.getAttendees().contains(user.getUserId()));
        assertFalse(room1.getAttendees().contains(user.getUserId()));
    }

    @Test
    public void login() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.username = "testUser";
        userLoginRequest.age = 22;
        userLoginRequest.interests = new ArrayList<>();
        userLoginRequest.interests.add("test");
        userLoginRequest.school = "Rice U";
        assertEquals("testUser", ChatAppAdapter.login(userLoginRequest).loginUser.getUserName());
    }

    @Test
    public void explore() {
        ExploreRequest exploreRequest = new ExploreRequest();
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // mock user id 1 join room 1, and room 2 is public
        builder = new User.Builder("adminTest");
        User adminTest = builder.build();
        Room.Builder builderRoom1 = new Room.Builder(adminTest);
        Room room1 = builderRoom1
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        Room.Builder builderRoom2 = new Room.Builder(adminTest);
        Room room2 = builderRoom2
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        RoomDB.addSessionRoom(room2.getRoomID(), room2);
        exploreRequest.userId = user.getUserId();
        if (user.joinChatRoom(room1.getRoomID())) {
            ExploreResponse response = ChatAppAdapter.explore(exploreRequest);
            assertEquals(room2.getRoomID(), response.roomList.get(0).getRoomID());
        }
    }

    @Test
    public void profile() {
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        ProfileRequest request = new ProfileRequest();
        request.userId = user.getUserId();
        UserDB.addUser(user.getUserId(), user);
        ProfileResponse response = ChatAppAdapter.profile(request);
        assertEquals(user.getUserName(), response.currUser.getUserName());
        assertEquals(user.getUserId(), response.currUser.getUserId());
    }

    @Test
    public void createRoom() {
        // create a user
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // create a room
        builder = new User.Builder("adminTest");
        User adminTest = builder.build();
        Room.Builder builderRoom = new Room.Builder(adminTest);
        Room room = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        CreateRoomRequest request = new CreateRoomRequest();
        request.isPrivate = false;
        request.roomName = "testroom";
        request.maxNumber = 2;
        request.roomDescription = "test";
        request.userId = user.getUserId();
        CreateRoomResponse response = ChatAppAdapter.createRoom(request);
        assertEquals(room.getRoomName(), response.room.getRoomName());
    }

    @Test
    public void join() {
        // create a user
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // create a room
        builder = new User.Builder("adminTest");
        User adminTest = builder.build();
        Room.Builder builderRoom = new Room.Builder(adminTest);
        Room room = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        if (user.joinChatRoom(room.getRoomID())) {
            JoinedRoomsRequest request = new JoinedRoomsRequest();
            request.userId = user.getUserId();
            JoinedRoomsResponse response = ChatAppAdapter.joinedRooms(request);
            assertEquals(room.getRoomID(), response.roomList.get(0).getRoomID());
        }

    }

    @Test
    public void leaveRoom() {
        // create a user
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // create a room
        builder = new User.Builder("adminTest");
        User adminTest = builder.build();
        Room.Builder builderRoom = new Room.Builder(adminTest);
        Room room = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        if (user.joinChatRoom(room.getRoomID())) {
            user.leaveRoom(room.getRoomID());
            LeaveRoomRequest request = new LeaveRoomRequest();
            request.roomId = room.getRoomID();
            request.userId = user.getUserId();
            assertTrue(ChatAppAdapter.leaveRoom(request).success);
        }


    }

    @Test
    public void switchRoom() {
        // create a user
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // create 2 rooms
        builder = new User.Builder("adminTest");
        User adminTest = builder.build();
        Room.Builder builderRoom = new Room.Builder(adminTest);
        Room room1 = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        Room room2 = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        RoomDB.addSessionRoom(room2.getRoomID(), room2);
        // join 2 rooms
        user.joinChatRoom(room1.getRoomID());
        user.joinChatRoom(room2.getRoomID());
        // switch to room2
        SwitchRoomRequest request = new SwitchRoomRequest();
        request.roomId = room2.getRoomID();
        request.userId = user.getUserId();
        SwitchRoomResponse response = ChatAppAdapter.switchRoom(request);
        assertEquals(room2.getRoomID(), response.room.getRoomID());
    }

    @Test
    public void invite() {
        // create a user
        User.Builder builder = new User.Builder("userTest1");
        User user = builder.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        // create 2 rooms
        Room.Builder builderRoom = new Room.Builder(user);
        Room room1 = builderRoom
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        // create another user
        User.Builder invited = new User.Builder("invited");
        User invitedUser = invited.age(22)
                .interests(new ArrayList<>())
                .school("Rice U")
                .build();
        UserDB.addUser(user.getUserId(), user);
        UserDB.addUser(invitedUser.getUserId(), invitedUser);
        InviteRequest request = new InviteRequest();
        request.adminId = user.getUserId();
        request.invitedId = invitedUser.getUserId();
        request.roomId = room1.getRoomID();
        InviteResponse response = ChatAppAdapter.invite(request);
        assertEquals(room1.getRoomID(), response.room.getRoomID());

    }

    ////////////////////////////////////////////////////////////
    @Test
    public void ban() {
        User.Builder builderad = new User.Builder("testadmin");
        User testadmin = builderad.age(22)
                .interests(new ArrayList<>())
                .school("RICE")
                .build();
        UserDB.addUser(testadmin.getUserId(), testadmin);
        User.Builder builderuser = new User.Builder("testuser");
        User testuser = builderuser.age(22)
                .interests(new ArrayList<>())
                .school("RICE")
                .build();
        UserDB.addUser(testuser.getUserId(), testuser);
        Room.Builder builder = new Room.Builder(testadmin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        testadmin.addChatRoomList(room.getRoomID());
        testuser.addChatRoomList(room.getRoomID());
        InviteRequest testrequest = new InviteRequest();
        testrequest.adminId = testadmin.getUserId();
        testrequest.roomId = room.getRoomID();
        testrequest.invitedId = testuser.getUserId();
        ChatAppAdapter.invite(testrequest);
        BanRequest testban = new BanRequest();
        testban.adminId = testadmin.getUserId();
        testban.roomId = room.getRoomID();
        testban.bannedId = testuser.getUserId();
        testban.banChoice = 1;
        ChatAppAdapter.ban(testban);
        SendMessageRequest testsend = new SendMessageRequest();
        testsend.roomId = room.getRoomID();
        testsend.senderId = testuser.getUserId();
        testsend.receiverId = -1;
        testsend.messageData = "Hello";
        ChatAppAdapter.sendMessage(testsend);
        List<AbsMessage> goal = RoomDB.getRoom(room.getRoomID()).getUserByID(testadmin.getUserId()).messageHistory;
        assertEquals("banedusertest", goal, RoomDB.getRoom(room.getRoomID()).getUserByID(testuser.getUserId()).messageHistory);
        BanRequest testunban = new BanRequest();
        testunban.adminId = testadmin.getUserId();
        testunban.roomId = room.getRoomID();
        testunban.bannedId = testuser.getUserId();
        testunban.banChoice = 2;
        ChatAppAdapter.ban(testunban);
        SendMessageRequest testsend2 = new SendMessageRequest();
        testsend2.roomId = room.getRoomID();
        testsend2.senderId = testuser.getUserId();
        testsend2.receiverId = -1;
        testsend2.messageData = "Hello";
        ChatAppAdapter.sendMessage(testsend2);
        List<AbsMessage> goal2 = RoomDB.getRoom(room.getRoomID()).getUserByID(testadmin.getUserId()).messageHistory;
        assertEquals("unbanedusertest", goal2, RoomDB.getRoom(room.getRoomID()).getMessageHistory());
    }

    @Test
    public void kick() {
        // create a user
        User admin = new User.Builder("testuser").build();
        UserDB.addUser(admin.getUserId(), admin);
        // create a room
        Room.Builder builder = new Room.Builder(admin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        // create a user2
        User user = new User.Builder("user").build();
        UserDB.addUser(user.getUserId(), user);
        // let user2 join room
        user.joinChatRoom(room.getRoomID());
        // let user hate count = 3
        KickRequest request = new KickRequest();
        user.setHateSpeechCount(3);
        request.kickeeId = user.getUserId();
        request.roomId = room.getRoomID();
        KickResponse response = ChatAppAdapter.kick(request);
        // check if room contains user
        assertFalse(room.getAttendees().contains(user.getUserId()));
    }

    @Test
    public void sendMessage() {
        // create a user
        User admin = new User.Builder("testuser").build();
        UserDB.addUser(admin.getUserId(), admin);
        // create a room
        Room.Builder builder = new Room.Builder(admin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        // create a user2
        User user = new User.Builder("user").build();
        UserDB.addUser(user.getUserId(), user);
        // let user2 join room
        user.joinChatRoom(room.getRoomID());
        // user1 send msg
        SendMessageRequest request = new SendMessageRequest();
        request.senderId = admin.getUserId();
        request.receiverId = -1;
        request.roomId = room.getRoomID();
        request.messageData = "hello test";
        // create response
        SendMessageResponse response = ChatAppAdapter.sendMessage(request);
        // assert response msg and msg sent before
        assertEquals(request.messageData, response.message.getMessage());
    }

    @Test
    public void recall() {
        // create a user
        User admin = new User.Builder("testuser").build();
        UserDB.addUser(admin.getUserId(), admin);
        // create a room
        Room.Builder builder = new Room.Builder(admin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        // create a user2
        User user = new User.Builder("user").build();
        UserDB.addUser(user.getUserId(), user);
        // let user2 join room
        user.joinChatRoom(room.getRoomID());
        // user1 send msg
        SendMessageRequest request = new SendMessageRequest();
        request.senderId = admin.getUserId();
        request.receiverId = -1;
        request.roomId = room.getRoomID();
        request.messageData = "hello test";
        // recall msg
        RecallMessageRequest recallMessageRequest = new RecallMessageRequest();
        recallMessageRequest.userId = admin.getUserId();
        recallMessageRequest.roomId = room.getRoomID();
        recallMessageRequest.messageId = room.getMessageHistory().get(0).getMessageId();
        // create response
        RecallMessageResponse response = ChatAppAdapter.recall(recallMessageRequest);
        // assert response msg and msg sent before
        assertNotSame(admin.getUserName() + " has recalled a message", room.getMessageHistory().get(0).getMessage());
    }

    @Test
    public void delete() {
        // create a user
        User admin = new User.Builder("testuser").build();
        UserDB.addUser(admin.getUserId(), admin);
        // create a room
        Room.Builder builder = new Room.Builder(admin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        // create a user2
        User user = new User.Builder("user").build();
        UserDB.addUser(user.getUserId(), user);
        // let user2 join room
        user.joinChatRoom(room.getRoomID());
        // user1 send msg
        SendMessageRequest request = new SendMessageRequest();
        request.senderId = admin.getUserId();
        request.receiverId = -1;
        request.roomId = room.getRoomID();
        request.messageData = "hello test";
        // delete msg
        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
        deleteMessageRequest.userId = admin.getUserId();
        deleteMessageRequest.roomId = room.getRoomID();
        deleteMessageRequest.messageId = room.getMessageHistory().get(0).getMessageId();
        // create response
        DeleteMessageResponse response = ChatAppAdapter.delete(deleteMessageRequest);
        // assert response msg and msg sent before
        assertEquals(admin.getUserName() + " has delete a message", room.getMessageHistory().get(0).getMessage());

    }

    @Test
    public void joinedRooms() {
        User testadmin = new User.Builder("testadmin").build();
        User testuser = new User.Builder("testuser").build();
        UserDB.addUser(testuser.getUserId(), testuser);
        UserDB.addUser(testadmin.getUserId(), testadmin);
        Room.Builder builder = new Room.Builder(testadmin);
        Room room1 = builder
                .maxNumber(2)
                .roomName("testroom1")
                .isPrivate(false)
                .roomDescription("test").build();
        testadmin.addChatRoomList(room1.getRoomID());
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        Room.Builder builder2 = new Room.Builder(testadmin);
        Room room2 = builder2
                .maxNumber(2)
                .roomName("testroom2")
                .isPrivate(false)
                .roomDescription("test2").build();
        RoomDB.addSessionRoom(room1.getRoomID(), room1);
        RoomDB.addSessionRoom(room2.getRoomID(), room2);
        testadmin.addChatRoomList(room2.getRoomID());
        JoinRoomRequest testjoin1 = new JoinRoomRequest();
        testjoin1.roomId = room1.getRoomID();
        testjoin1.userId = testuser.getUserId();
        JoinRoomRequest testjoin2 = new JoinRoomRequest();
        testjoin2.roomId = room2.getRoomID();
        testjoin2.userId = testuser.getUserId();
        ChatAppAdapter.join(testjoin1);
        ChatAppAdapter.join(testjoin2);
        JoinedRoomsRequest testjoined = new JoinedRoomsRequest();
        testjoined.userId = testuser.getUserId();
        JoinedRoomsRequest testjoined2 = new JoinedRoomsRequest();
        testjoined2.userId = testadmin.getUserId();
        List<Room> goallist = new ArrayList<>();
        goallist.add(room1);
        goallist.add(room2);
        List<Room> testlist = ChatAppAdapter.joinedRooms(testjoined).roomList;
        List<Room> testlist2 = ChatAppAdapter.joinedRooms(testjoined2).roomList;

        assertEquals("joinedroomtest1", goallist, testlist);
        assertEquals("joinedroomtest2", goallist, testlist2);
    }

    @Test
    public void getRoom() {
        User testadmin = new User.Builder("testadmin").build();
        User testuser = new User.Builder("testuser").build();
        UserDB.addUser(testuser.getUserId(), testuser);
        UserDB.addUser(testadmin.getUserId(), testadmin);
        Room.Builder builder = new Room.Builder(testadmin);
        Room room = builder
                .maxNumber(2)
                .roomName("testroom1")
                .isPrivate(false)
                .roomDescription("test").build();
        RoomDB.addSessionRoom(room.getRoomID(), room);
        testadmin.addChatRoomList(room.getRoomID());
        JoinRoomRequest testjoin1 = new JoinRoomRequest();
        testjoin1.roomId = room.getRoomID();
        testjoin1.userId = testuser.getUserId();
        ChatAppAdapter.join(testjoin1);
        GetRoomRequest testgetroom1 = new GetRoomRequest();
        testgetroom1.roomId = room.getRoomID();
        testgetroom1.userId = testuser.getUserId();
        GetRoomRequest testgetroom2 = new GetRoomRequest();
        testgetroom2.roomId = room.getRoomID();
        testgetroom2.userId = testuser.getUserId();
        assertEquals("getroom test for user", room, ChatAppAdapter.getRoom(testgetroom1).room);
        assertEquals("getroom test for admin", room, ChatAppAdapter.getRoom(testgetroom2).room);
    }

}