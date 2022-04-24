package edu.rice.comp504;

import com.google.gson.JsonObject;
import edu.rice.comp504.protocols.request.*;
import spark.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.rice.comp504.controller.Main.*;
import static edu.rice.comp504.adapter.MsgToClientSender.gson;

/**
 * parse the request string into a request object.
 */
public class RequestParser {

    /**
     * parse the http request string into a request object.
     */
    public static AbsAppRequest parseHttpRequest(String endpoint, Request httpRequest) {
        AbsAppRequest request = null;
        String body;
        String[] params;
        System.out.println("requestBody" + httpRequest.body());
        System.out.println("requestParams" + httpRequest.params());
        switch (endpoint) {
            case LOGIN:
                UserLoginRequest userLoginRequest = new UserLoginRequest();
                body = httpRequest.body();
                params = body.split("&");
                String username = params[0].split("=")[1];
                String age = params[1].split("=")[1];
                String listInterests = params[2].split("=")[1];
                //todo： Json String 转array
                List<String> interests = new ArrayList<>(Arrays.asList(listInterests.split(",")));
                String school = params[3].split("=")[1];
                userLoginRequest.age = Integer.parseInt(age);
                userLoginRequest.username = username.replace("+", " ");
                userLoginRequest.school = school;
                userLoginRequest.interests = interests;
                request = userLoginRequest;
                break;
            case EXPLORE:
                ExploreRequest exploreRequest = new ExploreRequest();
                body = httpRequest.body();
                params = body.split("&");
                String userid = params[0].split("=")[1];
                exploreRequest.userId = Integer.parseInt(userid);
                request = exploreRequest;
                break;
            case PROFILE:
                ProfileRequest profileRequest = new ProfileRequest();
                body = httpRequest.body();
                params = body.split("&");
                profileRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                request = profileRequest;
                break;
            case CREATE:
                CreateRoomRequest createRoomRequest = new CreateRoomRequest();
                body = httpRequest.body();
                params = body.split("&");
                String isPrivate = params[0].split("=")[1];
                String userId = params[1].split("=")[1];
                String roomCapacity = params[2].split("=")[1];
                String roomName = params[3].split("=")[1];
                String roomDescription = params[4].split("=")[1];
                createRoomRequest.isPrivate = isPrivate.equals("true") ? true : false;
                createRoomRequest.userId = Integer.parseInt(userId);
                createRoomRequest.roomName = roomName;
                createRoomRequest.maxNumber = Integer.parseInt(roomCapacity);
                createRoomRequest.roomDescription = roomDescription;
                request = createRoomRequest;
                break;

            case JOIN:
                JoinRoomRequest joinRoomRequest = new JoinRoomRequest();
                body = httpRequest.body();
                params = body.split("&");
                joinRoomRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                joinRoomRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                request = joinRoomRequest;
                break;
            case LEAVE:
                LeaveRoomRequest leaveRoomRequest = new LeaveRoomRequest();
                body = httpRequest.body();
                params = body.split("&");
                leaveRoomRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                leaveRoomRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                request = leaveRoomRequest;
                break;
            case LEAVEAll:
                LeaveAllRoomRequest leaveAllRoomRequest = new LeaveAllRoomRequest();
                body = httpRequest.body();
                params = body.split("&");
                leaveAllRoomRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                request = leaveAllRoomRequest;
                break;
            case BAN:
                BanRequest banRequest = new BanRequest();
                body = httpRequest.body();
                params = body.split("&");
                banRequest.roomId = Integer.parseInt(params[0].split("=")[1]);
                banRequest.bannedId = Integer.parseInt(params[1].split("=")[1]);
                banRequest.banChoice = Integer.parseInt(params[2].split("=")[1]);
                banRequest.adminId = Integer.parseInt(params[3].split("=")[1]);
                request = banRequest;
                break;
            case INVITE:
                InviteRequest inviteRequest = new InviteRequest();
                body = httpRequest.body();
                params = body.split("&");
                inviteRequest.adminId = Integer.parseInt(params[0].split("=")[1]);
                inviteRequest.invitedId = Integer.parseInt(params[1].split("=")[1]);
                inviteRequest.roomId = Integer.parseInt(params[2].split("=")[1]);
                request = inviteRequest;
                break;
            case KICK:
                KickRequest kickRequest = new KickRequest();
                body = httpRequest.body();
                params = body.split("&");
                kickRequest.kickeeId = Integer.parseInt(params[0].split("=")[1]);
                kickRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                request = kickRequest;
                break;
            case SWITCHROOM:
                body = httpRequest.body();
                params = body.split("&");
                SwitchRoomRequest switchRoomRequest = new SwitchRoomRequest();
                switchRoomRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                switchRoomRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                request = switchRoomRequest;
                break;
            case JOINEDROOMS:
                JoinedRoomsRequest joinedRoomsRequest = new JoinedRoomsRequest();
                body = httpRequest.body();
                params = body.split("&");
                joinedRoomsRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                request = joinedRoomsRequest;
                break;
            case GETUNJOINUSERS:
                GetUsersToInviteRequest getUsersToInviteRequest = new GetUsersToInviteRequest();
                body = httpRequest.body();
                params = body.split("&");
                getUsersToInviteRequest.roomId = Integer.parseInt(params[0].split("=")[1]);
                request = getUsersToInviteRequest;
                break;
            case GET_ROOMS:
                GetRoomRequest getRoomRequest = new GetRoomRequest();
                body = httpRequest.body();
                params = body.split("&");
                getRoomRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                System.out.println(Arrays.toString(params[1].split("=")));
                getRoomRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                request = getRoomRequest;
                break;
            case SEND:
                SendMessageRequest sendMessageRequest = new SendMessageRequest();
                body = httpRequest.body();
                JsonObject req = gson.fromJson(body, JsonObject.class);
                sendMessageRequest.senderId = req.get("senderId").getAsInt();
                sendMessageRequest.receiverId = req.get("receiverId").getAsInt();
                sendMessageRequest.roomId = req.get("roomId").getAsInt();
                sendMessageRequest.messageData = req.get("messageData").getAsString();
//                params = body.split("&");
//                sendMessageRequest.senderId = Integer.parseInt(params[0].split("=")[1]);
//                sendMessageRequest.receiverId = Integer.parseInt(params[1].split("=")[1]);
//                sendMessageRequest.roomId = Integer.parseInt(params[2].split("=")[1]);
//                sendMessageRequest.messageData = params[3].split("=")[1];
                request = sendMessageRequest;
                break;
            case RECALL:
                RecallMessageRequest recallMessageRequest = new RecallMessageRequest();
                body = httpRequest.body();
                params = body.split("&");
                recallMessageRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                recallMessageRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                recallMessageRequest.messageId = Integer.parseInt(params[2].split("=")[1]);
                request = recallMessageRequest;
                break;
            case DELETE:
                DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest();
                body = httpRequest.body();
                params = body.split("&");
                deleteMessageRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                deleteMessageRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                deleteMessageRequest.messageId = Integer.parseInt(params[2].split("=")[1]);
                request = deleteMessageRequest;
                break;
            case EDIT:
                EditRequest editRequest = new EditRequest();
                body = httpRequest.body();
                params = body.split("&");
                editRequest.userId = Integer.parseInt(params[0].split("=")[1]);
                editRequest.roomId = Integer.parseInt(params[1].split("=")[1]);
                editRequest.messageId = Integer.parseInt(params[2].split("=")[1]);
                editRequest.changedBody = params[3].split("=")[1];
                request = editRequest;
                break;
            default:
                break;
        }
        return request;
    }

    /**
     * parse the http request string into a request object.
     */
    public static AbsAppRequest parseWebSocketRequest(String message) {
        //根据type 去生成不同的Request对象

        return null;
    }
}
