function createNewRoom() {
    console.log("come in");
    let roomName = $("#room-name").val();
    if (roomName === "") {
        $("#create-room-alert").show();
        return;
    }
    let roomSize = $("#room-size").val();
    if (roomSize === "" || isNaN(roomSize) || roomSize <= 0) {
        $("#create-room-alert").show();
        return;
    }
    let roomDescription = $("#room-description").val();
    if (roomDescription === null || roomDescription === "") {
        $("#create-room-alert").show();
        return;
    }
    let roomType;
    if ($('input[type="radio"][id="public"]:checked').val()) {
        roomType = false;
    } else if ($('input[type="radio"][id="private"]:checked').val()) {
        roomType = true;
    } else {
        $("#create-room-alert").show();
        return;
    }
    $("#create-room-alert").hide();
    console.log("data get into");
    $.post("/room/create", {
        isPrivate: roomType,
        userId: localStorage.getItem("userId"),
        roomCapacity: roomSize,
        roomName: roomName,
        roomDescription: roomDescription
    }, function (data) {
        //restore the input to empty
        // loadMainPage();
        $('#room-name').val('');
        $('#room-size').val('');
        $('#room-description').val('');
        $('#public').prop("checked", false);
        $('#private').prop("checked", false);
        $("#modalCreateRoomForm").modal('toggle');
        console.log("room", data);
    }, "json");
}

//load all public rooms that the user is not in
function loadAllRooms(userId) {
    let allRooms = $("#room-list");
    console.log("user id", userId);
    $.post("/explore", {userId: userId}, function (data) {
        console.log("data is ", data);
        allRooms.empty();
        for (let i = 0; i < data.roomList.length; i++) {
            let room = data.roomList[i];
            let roomTemp = templateRoom;
            const roomName = room.roomName;
            const currentUserCounts = Object.values(room.attendees).length;
            const roomSize = "Online member: " + currentUserCounts;
            const roomDescription = room.roomDescription;
            const roomId = "joinRoom(" + room.roomId + ")";
            console.log(roomName);
            console.log(roomSize);
            console.log(roomDescription);
            roomTemp = roomTemp.replace('joinRoom(3)', roomId);
            roomTemp = roomTemp.replace('Room 1', parseAddSignToSpace(roomName));
            roomTemp = roomTemp.replace("Online member: 3", roomSize);
            roomTemp = roomTemp.replace("Please join us", parseAddSignToSpace(roomDescription));
            allRooms.append(roomTemp);
        }
    }, "json");

}

let templateRoom = "<div class=\"col-lg-4 mb-2\">\n" +
    "    <div class=\"card mx-auto\" style=\"width:22rem;\">\n" +
    "        <div class=\"card-body\">\n" +
    "            <h5 class=\"card-title\">\n" +
    "                Room 1\n" +
    "            </h5>\n" +
    "            <p class=\"card-text\">\n" +
    "                Online member: 3\n" +
    "            </p>\n" +
    "            <p class=\"card-text\"><small class=\"text-muted\">Please join us</small>\n" +
    "            </p>\n" +
    "            <a href=\"#\" class=\"btn btn-success\" id=\'current-room-id\' onclick=\'joinRoom(3)\'>\n" +
    "                Join\n" +
    "            </a>\n" +
    "        </div>\n" +
    "    </div>\n" +
    "</div>"

//user join a public room
joinRoom = function (roomId) {
    console.log("roomId: ", roomId);
    $.post("/room/join", {userId: localStorage.getItem("userId"), roomId: roomId}, function (data) {
        if (!data.success) {
            return;
        }
        console.log("room data is", data);
        loadMainPage();
    }, "json");
}




