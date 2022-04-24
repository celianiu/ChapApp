package edu.rice.comp504.controller;

import com.google.gson.Gson;
import edu.rice.comp504.RequestParser;
import edu.rice.comp504.adapter.WebSocketAdapter;
import edu.rice.comp504.adapter.ChatAppAdapter;
import edu.rice.comp504.protocols.request.*;
import edu.rice.comp504.protocols.response.*;

import static spark.Spark.*;

public class Main {
    public static final String LOGIN = "/login";
    public static final String JOINEDROOMS = "/chatroom/joinedRooms";
    public static final String EXPLORE = "/explore";
    public static final String PROFILE = "/profile";
    public static final String CREATE = "/room/create";
    public static final String JOIN = "/room/join";
    public static final String LEAVE = "/room/leave";
    public static final String LEAVEAll = "/room/leaveAll";
    public static final String BAN = "/room/ban";
    public static final String INVITE = "/room/invite";
    public static final String KICK = "/room/kick";
    public static final String SWITCHROOM = "/room/switch";
    public static final String SEND = "/room/send";
    public static final String RECALL = "/room/recall";
    public static final String DELETE = "/room/delete";
    public static final String EDIT = "/room/edit";
    public static final String GET_ROOMS = "/chatroom/getRoom";
    public static final String GETUNJOINUSERS = "/chatroom/getUnjoinUsers";

    /**
     * Chat App entry point.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFiles.location("/public");
        // Timeout 60 minutes
        webSocketIdleTimeoutMillis(3600000);
        webSocket("/chatapp", WebSocketAdapter.class);
        init();
        Gson gson = new Gson();

        post(LOGIN, (request, response) -> {
            UserLoginRequest loginRequest = (UserLoginRequest) RequestParser.parseHttpRequest(LOGIN, request);
            UserLoginResponse loginResponse = ChatAppAdapter.login(loginRequest);
            return gson.toJson(loginResponse);
        });

        post(EXPLORE, (request, response) -> {
            //todo:
            ExploreRequest exploreRequest = (ExploreRequest) RequestParser.parseHttpRequest(EXPLORE, request);
            ExploreResponse exploreResponse = ChatAppAdapter.explore(exploreRequest);
            return gson.toJson(exploreResponse);
        });

        post(PROFILE, (request, response) -> {
            //todo:
            ProfileRequest profileRequest = (ProfileRequest) RequestParser.parseHttpRequest(PROFILE, request);
            ProfileResponse profileResponse = ChatAppAdapter.profile(profileRequest);
            return gson.toJson(profileResponse);
        });

        post(CREATE, (request, response) -> {
            //todo:
            CreateRoomRequest createRoomRequest = (CreateRoomRequest) RequestParser.parseHttpRequest(CREATE,
                    request);
            CreateRoomResponse createRoomResponse = ChatAppAdapter.createRoom(createRoomRequest);
            return gson.toJson(createRoomResponse);
        });

        post(GET_ROOMS, (request, response) -> {
            //todo:
            GetRoomRequest getRoomRequest = (GetRoomRequest) RequestParser.parseHttpRequest(GET_ROOMS,
                    request);
            /**
             * 实际代码
             *    GetRoomResponse getRoomResponse = ChatAppController.getRoom(getRoomRequest);
             */
            GetRoomResponse getRoomResponse = ChatAppAdapter.getRoom(getRoomRequest);
            //mock 代码
//            GetRoomResponse getRoomResponse = new GetRoomResponse();
//            getRoomResponse.room = MockData.mockRoom();
            return gson.toJson(getRoomResponse);
        });

        post(GETUNJOINUSERS, (request, response) -> {
            //todo:
            GetUsersToInviteRequest getUsersToInviteRequest = (GetUsersToInviteRequest) RequestParser.parseHttpRequest(GETUNJOINUSERS,
                    request);
            GetUsersToInviteResponse getUsersToInviteResponse = ChatAppAdapter.getInvitableUser(getUsersToInviteRequest);
            return gson.toJson(getUsersToInviteResponse);
        });


        post(JOIN, (request, response) -> {
            //todo:
            //把加入的Room 对象 传给前端
            JoinRoomRequest joinRoomRequest = (JoinRoomRequest) RequestParser.parseHttpRequest(JOIN,
                    request);
            JoinRoomResponse joinRoomResponse = ChatAppAdapter.join(joinRoomRequest);
            return gson.toJson(joinRoomResponse);
        });

        post(JOINEDROOMS, ((request, response) -> {
            JoinedRoomsRequest joinedRoomsRequest = (JoinedRoomsRequest) RequestParser.parseHttpRequest(JOINEDROOMS,
                    request);
            /**
             * 实际代码
             * JoinedRoomsResponse joinedRoomsResponse = ChatAppController.joinedRooms(joinedRoomsRequest);
             */

            JoinedRoomsResponse joinedRoomsResponse = ChatAppAdapter.joinedRooms(joinedRoomsRequest);

//            //mock 代码
//            JoinedRoomsResponse joinedRoomsResponse = new JoinedRoomsResponse();
//            List<Room> joinedRooms = new ArrayList<>();
//            User.Builder ub = new User.Builder("Mack Joyner");
//            List<String> interests = new ArrayList<>();
//            interests.add("Coding");
//            interests.add("Cooking");
//            User mack = ub.age(20).school("Rice University").interests(interests).build();
//            Room.Builder builder = new Room.Builder(mack);
//            Room mock_room = builder.isPrivate(false).maxNumber(5).roomName("mock room").build();
//            joinedRooms.add(mock_room);
//            joinedRoomsResponse.roomList =joinedRooms;
            return gson.toJson(joinedRoomsResponse);
        }));

        post(SWITCHROOM, (request, response) -> {
            SwitchRoomRequest switchRoomRequest = (SwitchRoomRequest) RequestParser.parseHttpRequest(SWITCHROOM,
                    request);
            SwitchRoomResponse switchRoomResponse = ChatAppAdapter.switchRoom(switchRoomRequest);
            return gson.toJson(switchRoomResponse);
        });

        post(LEAVE, (request, response) -> {
            LeaveRoomRequest leaveRoomRequest = (LeaveRoomRequest) RequestParser.parseHttpRequest(LEAVE,
                    request);
            LeaveRoomResponse leaveRoomResponse = ChatAppAdapter.leaveRoom(leaveRoomRequest);
            return gson.toJson(leaveRoomResponse);
        });

        post(LEAVEAll, (request, response) -> {
            LeaveAllRoomRequest leaveAllRoomRequest = (LeaveAllRoomRequest) RequestParser.parseHttpRequest(LEAVEAll,
                    request);
            LeaveAllRoomResponse leaveAllRoomResponse = ChatAppAdapter.leaveAllRoom(leaveAllRoomRequest);
            return gson.toJson(leaveAllRoomResponse);
        });

        post(BAN, (request, response) -> {
            BanRequest banRequest = (BanRequest) RequestParser.parseHttpRequest(BAN,
                    request);
            BanResponse banResponse = ChatAppAdapter.ban(banRequest);
            return gson.toJson(banResponse);
        });

        post(KICK, (request, response) -> {
            KickRequest kickRequest = (KickRequest) RequestParser.parseHttpRequest(KICK,
                    request);
            KickResponse kickResponse = ChatAppAdapter.kick(kickRequest);
            return gson.toJson(kickResponse);
        });

        post(INVITE, (request, response) -> {
            //todo:
            InviteRequest inviteRequest = (InviteRequest) RequestParser.parseHttpRequest(INVITE,
                    request);
            InviteResponse inviteResponse = ChatAppAdapter.invite(inviteRequest);
            return gson.toJson(inviteResponse);
        });

        post(SEND, (request, response) -> {
            //todo:
            System.out.println(request.body());
            SendMessageRequest sendMessageRequest = (SendMessageRequest) RequestParser.parseHttpRequest(SEND,
                    request);
            SendMessageResponse sendMessageResponse = ChatAppAdapter.sendMessage(sendMessageRequest);
            return gson.toJson(sendMessageResponse);
        });

        post(RECALL, (request, response) -> {
            //todo:
            RecallMessageRequest recallMessageRequest = (RecallMessageRequest) RequestParser.parseHttpRequest(RECALL,
                    request);
            RecallMessageResponse recallMessageResponse = ChatAppAdapter.recall(recallMessageRequest);
            return gson.toJson(recallMessageResponse);
        });
        post(DELETE, (request, response) -> {
            //todo:
            DeleteMessageRequest deleteMessageRequest = (DeleteMessageRequest) RequestParser.parseHttpRequest(DELETE,
                    request);
            DeleteMessageResponse deleteMessageResponse = ChatAppAdapter.delete(deleteMessageRequest);
            return gson.toJson(deleteMessageRequest);
        });
        post(EDIT, (request, response) -> {
            //todo:
            EditRequest editRequest = (EditRequest) RequestParser.parseHttpRequest(EDIT,
                    request);
            EditResponse editResponse = ChatAppAdapter.edit(editRequest);
            return gson.toJson(editResponse);
        });


        post("/chatroom/getAllMessages", ((request, response) -> {
            //todo:
            return gson.toJson(null);
        }));


    }

    /**
     * Get the heroku assigned port number.
     *
     * @return The heroku assigned port number
     */
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; // return default port if heroku-port isn't set.
    }
}
