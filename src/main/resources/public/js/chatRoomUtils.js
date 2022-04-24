let privateRoomIconTemplate = '<i class="fas fa-lock"></i>';
let roomAdminIconTemplate = '<i class="fas fa-users-cog"></i>'
let userTemplate = '<li class="clearfix">\n' +
    '                                <div class="name dropdown" id="ROOM-USERLIST-USERNAME-ID"> ROOM-USERLIST-USERNAME ' +
    '                                    <span>{ADMIN-ICON}</span>' +
    '                                    <div class = "dropdown-content" id="USER-DETAIL">\n' +
    '                                        <p>Age: USER-AGE</p>\n' +
    '                                        <p>School: USER-SCHOOL</p>\n' +
    '                                        <p>Interests: USER-INTERESTS</p>\n' +
    '                                    </div>\n' +
    '                                </div>\n' +
    '                            </li>'

// let roomTemplate = '<div class ="mt-3 w-100">\n' +
//     '                <button type="button" id="ROOM-BTN" class="btn btn-light room-list-btn">ROOM-NAME\n' +
//     '                    <span id="{ROOM-NTF-ID}"class="adminOptsNone roomlist-notification" id="{ROOM-NTF-ID}">1</span>\n' +
//     '                    <span><button id ="{LEAVE-ROOM}" class="btn btn-sm"><i class="fas fa-sign-out-alt"></i></button></span>\n' +
//     '                </button>\n' +
//     '                </div>';

// let roomTemplate = '<div class ="mt-3 w-100">\n' +
//     '                <button type="button" id="ROOM-BTN" class="btn btn-light"><span>ROOM-NAME</span>' +
//     '                    <span id="{ROOM-NTF-ID}"class="adminOptsNone roomlist-notification" id="{ROOM-NTF-ID}">1</span>\n' +
//     '                    <span><i id ="{LEAVE-ROOM}" class="fas fa-sign-out-alt ml-4""></i></span>\n' +
//     '                </button>\n' +
//     '                </div>';

let roomTemplate = '<div class ="mt-3 w-100">\n' +
    '                <div class="btn btn-light"><span id="ROOM-BTN">ROOM-NAME</span>' +
    '                    <span id="{ROOM-NTF-ID}" class="adminOptsNone roomlist-notification" id="{ROOM-NTF-ID}">1</span>\n' +
    '                    <span><i id ="{LEAVE-ROOM}" class="fas fa-sign-out-alt ml-4""></i></span>\n' +
    '                </div>\n' +
    '                </div>';
let roomListDefault = '<button id="default-room-list-btn" type="button" class="btn btn-light mt-3" onclick="loadExplore()">Explore rooms~</button>'
let leaveAllRoomBtn = '<button id="leave-all-rooms-btn" class="btn btn-danger mt-5" onclick="leaveAllRoom()">Leave all room</button>';


let adminTitle = '<li class="adminOptsDisplay" style="padding-top: 80%; width: 100%">\n' +
    '                                <h5>Manage</h5>\n' +
    '                            </li>';

let blockOptionTemplate = '<button class="dropdown-item" id="BLOCK-USER-BTN">USERNAME</button>'
let inviteOptionTemplate = '<button class="dropdown-item" id="INVITE-USER-BTN">USERNAME</button>'
let unblockOptionTemplate = '<button class="dropdown-item" id="UNBLOCK-USER-BTN">USERNAME</button>'

let deleteBtnTemplate = '<button class="redoBtn" id="{msg-delete-icon}"><i class="fas fa-trash-alt"></i></i></button>';
let selfMessageTemplate = '<li class="clearfix" id="{msg-block-id}">' +
    '                                <div class="message-data text-right">\n' +
    '                                    <span class="message-data-time">{sender} to {receiver}</span>\n' +
    '                                </div>\n' +
    '                                <div class="message my-message float-right">  <span id="{contentid}" ' +
    '                                   class="edit-on-click"> {content}</span>' +
    '                                    <button class="redoBtn"><i id="{msg-redo}" class="fas fa-redo "></i></button>' +
    '                                    {msg-delete-btn}\n' +
    '                                <span><div class="text-right msg-timestamp"> {timestamp} </div></span> </div>\n' +
    '                            </li>'
let otherMessageTemplate = '<li class="clearfix" id="{msg-block-id}">\n' +
    '                                <div class="message-data">\n' +
    '                                    <span class="message-data-time">{sender} to {receiver}</span>\n' +
    '                                </div>\n' +
    '                                <div class="message other-message">{content}\n' +
    '                                    {msg-delete-btn}\n' +
    '                                <span><div class="text-right msg-timestamp"> {timestamp} </div></span> </div>\n' +
    '                            </li>'

let receiverListTemplate = '                                <option value="all">All users</option>\n';
let msgHistoryNotificationTemplate = '<li class = "chatbox-notification" >{content}</li>'

// default values
let chatRoomTitleDefaultValue = '<h5 class="m-b-0" id="chatroom-name">No Chat Room Selected</h5>';

function displayAdminOpts() {
    $('#admin-opts').removeClass("adminOptsNone");
    $('#admin-opts').addClass("adminOptsDisplay");
    $('#admin-block-btn').empty();
    $('#admin-invite-btn').empty();
}

function removeAdminOpts() {
    $('#admin-opts').removeClass("adminOptsDisplay");
    $('#admin-opts').addClass("adminOptsNone");
}

function buildInterestsStr(interests) {
    let str = "";
    for (let i = 0; i < interests.length; i++) {
        if (i == interests.length - 1) {
            str = str + interests[i];
        } else {
            str = str + interests[i] + ", ";
        }
    }
    return str.length === 0 ? "Guess" : str;
}

function parseAddSignToSpace(data) {
    let newData = data.replace("+", " ");
    while (newData !== data) {
        data = newData;
        newData = data.replace("+", " ");
    }
    return newData;
}


function clearChatRoomTitle() {
    $('#chat-room-title').empty();
    $('#chat-room-title').append(chatRoomTitleDefaultValue);
}

function clearAllChatBox() {
    $('#chat-history').empty();
    $('#receiver-selector').empty();
    clearChatRoomTitle();
}


function clickToModify(e) {
    var $text = $(this),
        $input = $('<input type="text" />')

    $text.hide()
        .after($input);

    $input.val($text.html()).show().focus()
        .keypress(function (e) {
            var key = e.which
            if (key == 13) // enter key
            {
                $input.hide();
                if ($input.val().length !== 0){
                    $text.html($input.val());
                }
                $text.show();
                return false;
            }
        })
        .focusout(function () {
            $input.hide();
            $text.show();
            let request = e.data;
            request.changedBody = $input.val();
            if ($input.val().length === 0) {
            } else {
                if ($text.html() !== $input.val()) {
                    $.post("/room/edit", request, function (data) {
                        console.log("edit message", data);
                    }, "json");
                }
                $text.html($input.val())
            }
        })
}

