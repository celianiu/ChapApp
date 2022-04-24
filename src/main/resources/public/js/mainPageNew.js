/**
 * send message use http request
 */


function sendMessage() {
    currentState = undefined;
    const messageData = $(".emoji-input").val();
    console.log("emoji is", messageData);
    // const messageData = $("#send-message-content").val();
    if (messageData === "") {
        return;
    }
    if (detectHate(messageData)) {
        console.log("come on!!");
        newWarnUser();
    } else {
        currentState = 1;
    }
    if (currentState === 2) {
        console.log("it has been kicked");
        return;
    }
    const senderId = userId;
    //add name to id
    const selected = $("#receiver-selector :selected");
    const receiverId = selected.val();
    //id is value,show is username
    const roomId = curRoom;
    console.log("selected person", receiverId);
    console.log("messageData", messageData);
    $.post("/room/send", JSON.stringify({
        senderId: senderId,
        receiverId: receiverId,
        roomId: roomId,
        messageData: messageData,
    }), function (data) {
        //build message
        let combinedData = {};
        combinedData.data = data.message.roomId;
        getRooms(combinedData);
        console.log(data);
        $(".emoji-input").html('');
        $('.msg_container_base').scrollTop($('.msg_container_base')[0].scrollHeight);
    }, "json");
}

/**
 * Warn a user for hate.
 */
function warnUser() {
    $.post("/room/kick", {kickeeId: userId, roomId: curRoom}, function (data) {
        //kick out
        console.log("kicked off", data);
        console.log(data.success);
        if (data.success) {
            //leave all rooms, reload the chatroom page
            renderChatroomOnStart(userId);
        } else {
            currentState = 1;
        }
    }, "json");
}

function newWarnUser() {
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function () {
        console.log("this request is ", this.responseText);
        if (this.responseText.includes("true")) {
            //leave all rooms, reload the chatroom page
            renderChatroomOnStart(userId);
            currentState = 2;
        } else {
            currentState = 1;
        }
    }
    xhttp.open("POST", "/room/kick", false);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    let request = "kickeeId=" + userId + "&" + "roomId=" + curRoom;
    xhttp.send(request);
}


function closeConnection() {
    cancelKeepAlive();
    logOut();
}

/**
 * Get chatroom information, on startup gets lowest id room.
 */
function renderChatroomOnStart(userId) {
    $.post("/chatroom/joinedRooms", {userId: userId}, function (data) {
        //clear current chat box
        clearRoomInfo();
        //Load general or show no room message if user not in General chat
        console.log("joinedRooms are", data);
        $('#allUserRooms').empty();
        let rooms = data.roomList
        let userRoomList = $('#allUserRooms');
        console.log("get all rooms", data.roomList)
        if (data.roomList === null || rooms.length === 0) {
            userRoomList.append(roomListDefault);
            return;
        }
        for (let i = 0; i < rooms.length; i++) {
            let room = rooms[i];
            let tempRoom = roomTemplate;
            let tempBtnId = "btn-room-" + room.roomId;
            tempRoom = tempRoom.replace('ROOM-NAME', parseAddSignToSpace(room.roomName));
            tempRoom = tempRoom.replace("ROOM-BTN", tempBtnId);
            tempRoom = tempRoom.replace("{LEAVE-ROOM}", tempBtnId + "-leave")
            tempRoom = tempRoom.replace("{ROOM-NTF-ID}", tempBtnId + "-ntf");

            userRoomList.append(tempRoom);
            tempBtnId = '#' + tempBtnId;
            $(tempBtnId).click(room.roomId, getRooms);
            $(tempBtnId + "-leave").click({userId: userId, roomId: room.roomId}, leaveRoom);
            if (room.isPrivate) {
                $(tempBtnId).css("background-color", "lightgrey");
            } else {
                // $(tempBtnId).css("background-color", "lightgrey");
            }
        }
        userRoomList.append(leaveAllRoomBtn);

    }, "json");
}

function leaveRoom(e) {
    const roomId = e.data.roomId;
    console.log("leave", roomId);
    $.post("/room/leave", e.data, function (data) {
        let room = "#btn-room-" + e.data.roomId;
        let leave = room + "-leave";
        let ntf = room + "-ntf";
        clearRoomInfo();
        $(room).remove();
        $(leave).remove();
        $(ntf).remove();
    })
}

function leaveAllRoom() {
    console.log("leave all", userId);
    $.post("/room/leaveAll", {userId: userId}, function (data) {
        renderChatroomOnStart(userId);
    })

}

/**
 * render select receiver box
 * @param userId
 * @param data
 */
function renderSelectReceiver(userId, attendees) {
    let receiverList = $('#receiver-selector');
    receiverList.empty();
    let everyOne = receiverListTemplate;
    everyOne = everyOne.replace("all", "-1");
    receiverList.append(everyOne);
    let users = Object.values(attendees);
    for (let i = 0; i < users.length; i++) {
        let currentUserId = users[i].user.userId;
        let currentUserName = users[i].user.userName;
        if (currentUserId == userId) {
        } else {
            let currentOne = receiverListTemplate;
            currentOne = currentOne.replace("all", currentUserId);
            currentOne = currentOne.replace("All users", parseAddSignToSpace(currentUserName));
            receiverList.append(currentOne);
        }
    }
}

/**
 * get current room info
 * @param e
 */
function getRooms(e) {
    let roomId = e.data;
    // set cur room
    console.log("post to get room: ", {userId: userId, roomId: e.data});
    $.post("/chatroom/getRoom", {userId: userId, roomId: e.data}, function (data) {
        console.log("current room info ", data);
        let isOwner = data.room.owner.userId == userId;
        console.log("get room", data)
        clearRoomInfo();
        curRoom = roomId;
        renderUsers(data.room.attendees, roomId, isOwner, data.room.owner.userId, data.room.bannedList);
        $('#chatroom-name').html(parseAddSignToSpace(data.room.roomName))
        removeNotification(roomId);
        console.log("userId is", userId);
        console.log("userId info", data.room.attendees[userId].messageHistory);
        renderMessages(data.room.attendees[userId].messageHistory, isOwner);
        renderSelectReceiver(userId, data.room.attendees);
        //show input and send
        $("#send-message-content").show();
        $("#send-message").show();
    }, "json")
}

function clearRoomInfo() {
    $('#roomUserList').empty();
    $('#chat-history').empty();
    $('#admin-invite-btn').empty();
    $('#admin-block-btn').empty();
    $('#admin-unblock-btn').empty();
    // $('#chat-history').empty();
    removeAdminOpts();
    clearAllChatBox();
    $("#send-message-content").hide();
    $("#send-message").hide();
}

/**
 * receive a new message from websocket, parse message and display
 * @param message
 */
function updateChatRoom(message) {
    console.log("new message id", message);
    //if it is a invite message,refresh current
    if (message.notificationType !== null && message.notificationType !== undefined && message.notificationType === "NOTIFICATION_INVITE") {
        //the invitee refresh the page
        console.log("it is invited", userId);
        if (message.receiverId == userId) {
            console.log("invitee is", userId);
            renderChatroomOnStart(userId);
            return;
        }
    }
    //when we get a message, we need to check if the message is in our room
    let isMessageInCurrRoom = (message.roomId == curRoom);
    //if the message is not in our room
    if (!isMessageInCurrRoom) {
        addOneNotification(message.roomId);
    } else {
        let combinedData = {};
        combinedData.data = message.roomId;
        getRooms(combinedData);
    }
}

function renderUsers(userMap, roomId, isOwner, ownerId, blockList) {
    let userListDiv = $('#roomUserList');
    users = Object.values(userMap);
    for (let i = 0; i < users.length; i++) {
        let user = users[i].user;
        let tempUser = userTemplate;
        let tempBlockId = "room-" + roomId + "user-" + user.userId;
        tempUser = tempUser.replace("ROOM-USERLIST-USERNAME-ID", tempBlockId);
        tempUser = tempUser.replace("ROOM-USERLIST-USERNAME", parseAddSignToSpace(user.userName))
        tempUser = tempUser.replace("USER-DETAIL", tempBlockId + "-detail")
        tempUser = tempUser.replace("USER-AGE", user.age);
        tempUser = tempUser.replace("USER-SCHOOL", parseAddSignToSpace(user.school));
        tempUser = tempUser.replace("USER-INTERESTS", buildInterestsStr(user.interests));
        if (user.userId == ownerId) {
            tempUser = tempUser.replace("{ADMIN-ICON}", roomAdminIconTemplate);
        } else {
            tempUser = tempUser.replace("{ADMIN-ICON}", "");
        }
        userListDiv.append(tempUser);
    }
    if (isOwner) {
        displayAdminOpts();
        let blockOptions = $('#admin-block-btn')
        renderBlockUsersList(users, roomId, blockList);
        renderInvitableUsersList(roomId);
    } else {
        removeAdminOpts();
    }

}

function renderBlockUsersList(users, roomId, blockList) {
    let blockOptions = $('#admin-block-btn')
    let unblockOptions = $('#admin-unblock-btn');
    for (let i = 0; i < users.length; i++) {
        let user = users[i].user;
        if (user.userId == userId) {
            continue;
        }
        let found = false;
        for (let i = 0; i < blockList.length; i++) {
            if (user.userId === blockList[i]) {
                found = true;
                break;
            }
        }
        if (!found) {
            // can be banned
            addNewBlockOption(roomId, user);
        } else {
            // can be unbanned
            addNewUnblockOption(roomId, user);
        }
        // addNewUnblockOption(roomId, user);
    }
}

function addNewBlockOption(roomId, user) {
    let blockOptions = $('#admin-block-btn')
    let tempBlockId = "room-" + roomId + "-block-" + user.userId;
    let tempBlockOpt = blockOptionTemplate;
    tempBlockOpt = tempBlockOpt.replace("BLOCK-USER-BTN", tempBlockId);
    tempBlockOpt = tempBlockOpt.replace("USERNAME", parseAddSignToSpace(user.userName));
    blockOptions.append(tempBlockOpt);
    tempBlockId = "#" + tempBlockId;
    $(tempBlockId).click({roomId: roomId, bannedId: user.userId, banChoice: 1, adminId: userId}, blockUser);
}

function addNewUnblockOption(roomId, user) {
    let unblockOptions = $('#admin-unblock-btn');
    let tempUnblockId = "room-" + roomId + "-unblock-" + user.userId;
    let tempUnblockOpt = unblockOptionTemplate;
    tempUnblockOpt = tempUnblockOpt.replace("UNBLOCK-USER-BTN", tempUnblockId);
    tempUnblockOpt = tempUnblockOpt.replace("USERNAME", user.userName);
    unblockOptions.append(tempUnblockOpt);
    tempUnblockId = "#" + tempUnblockId;
    $(tempUnblockId).click({roomId: roomId, bannedId: user.userId, banChoice: 2, adminId: userId}, blockUser);
}

function renderInvitableUsersList(roomId) {
    let inviteOpt = $('#admin-invite-btn')
    $.post('/chatroom/getUnjoinUsers', {roomId, roomId}, function (data) {
        let users = data.userList;
        for (let i = 0; i < users.length; i++) {
            let user = users[i];
            if (user.userId === userId) {
                continue;
            }
            let tempInviteId = "room-" + roomId + "-invite-" + user.userId;
            let tempBlockOpt = inviteOptionTemplate;
            tempBlockOpt = tempBlockOpt.replace("INVITE-USER-BTN", tempInviteId);
            tempBlockOpt = tempBlockOpt.replace("USERNAME", user.userName);
            inviteOpt.append(tempBlockOpt);
            tempInviteId = "#" + tempInviteId;
            $(tempInviteId).click({adminId: userId, invitedId: user.userId, roomId: roomId}, inviteUser);
        }
    }, "json");
}

function blockUser(e) {
    $.post('/room/ban', e.data, function (data) {
        console.log("block: ", data);
    }, "json");
}

function inviteUser(e) {
    $.post("/room/invite", e.data, function (data) {
        console.log("invite", data);
    }, "json");
}

function renderMessages(msgList, isOwner) {
    for (let i = 0; i < msgList.length; i++) {
        let message = msgList[i];
        if (message.messageType === "PUBLIC_MESSAGE" || message.messageType === "PRIVATE_MESSAGE") {
            buildOneMessage(message, isOwner);
        } else if (message.messageType === "ROOM_INFO_MESSAGE") {
            buildOneNotification(message);
        }
    }
}

/**
 * Detect whether the message contains hate word.
 * @param data
 */
function detectHate(data) {
    return data.includes("hate");
}

function buildOneMessage(message, isOwner) {
    let chatHistory = $('#chat-history');
    let tempMsg = "";
    let tempId = "msg-" + message.messageId;
    if (message.senderId === userId) {
        tempMsg = selfMessageTemplate
        tempMsg = tempMsg.replace("{msg-redo}", tempId + "-redo")
        tempMsg = tempMsg.replace("{contentid}", tempId + "-content");
    } else {
        tempMsg = otherMessageTemplate;
    }
    tempMsg = tempMsg.replace("{sender}", parseAddSignToSpace(message.senderName));
    tempMsg = tempMsg.replace("{timestamp}", new Date(message.time).toLocaleString())
    tempMsg = tempMsg.replace("{receiver}", parseAddSignToSpace(message.receiverName));
    tempMsg = tempMsg.replace("{content}", parseAddSignToSpace(message.message));
    tempMsg = tempMsg.replace("{msg-block-id}", tempId);
    tempMsg = tempMsg.replace("{msg-block-id}", tempId);

    if (isOwner) {
        tempMsg = tempMsg.replace("{msg-delete-btn}", deleteBtnTemplate);
        tempMsg = tempMsg.replace("{msg-delete-icon}", tempId + "-delete");
    } else {
        tempMsg = tempMsg.replace("{msg-delete-btn}", "");
    }
    chatHistory.append(tempMsg);
    if (message.senderId === userId) {
        let tempRedoId = "#" + tempId + "-redo";
        $(tempRedoId).click({userId: userId, roomId: curRoom, messageId: message.messageId}, recallMsg);
        let tempContentId = "#" + tempId + "-content";
        $(tempContentId).click({userId: userId, roomId: curRoom, messageId: message.messageId}, clickToModify);
    }
    if (isOwner) {
        let tempDeleteId = "#" + tempId + "-delete";
        $(tempDeleteId).click({userId: userId, roomId: curRoom, messageId: message.messageId}, deleteMsg);
    }

}

function buildOneNotification(msg) {
    let chatHistory = $('#chat-history');
    let tempNtf = msgHistoryNotificationTemplate;
    tempNtf = tempNtf.replace("{content}", parseAddSignToSpace(msg.message));
    chatHistory.append(tempNtf);
}

function recallMsg(e) {
    $.post("/room/recall", e.data, function (data) {
        console.log("recall", e.data);
    }, "json");

}

function deleteMsg(e) {
    $.post("/room/delete", e.data, function (data) {
        console.log("delete", e.data);
    }, "json");
}

function addOneNotification(roomId) {
    let notificationId = "#btn-room-" + roomId + "-ntf";
    if ($(notificationId).hasClass("adminOptsNone")) {
        $(notificationId).removeClass("adminOptsNone");
    } else {
        let ntf = JSON.parse($(notificationId).html());
        $(notificationId).html(ntf + 1);
    }
}

function removeNotification(roomId) {
    let notificationId = "#btn-room-" + roomId + "-ntf";
    $(notificationId).html(1);
    $(notificationId).addClass("adminOptsNone");
}

function renderSendTo() {

}






