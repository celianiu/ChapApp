let userId = null;
let curRoom = -1;
let websocket = null;
let currentState = undefined;

window.onload = function () {
    //check if has localstorage
    userId = JSON.parse(localStorage.getItem("userId"));
    if (userId === null) {
        location.href = "index.html";
        return;
    }
    //build connection
    websocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chatapp?userId=" + userId);
    websocket.onclose = () => closeConnection();
    websocket.onmessage = (msg) => updateChatRoom(JSON.parse(msg.data));
    keepAlive();
    //mainPage
    $("#send-message").click(sendMessage);
    //load all rooms-explore
    $("#createRoom").click(createNewRoom);
    //profile
}

let timerId = -1;

function keepAlive(timeout = 20000) {
    if (websocket.readyState == websocket.OPEN) {
        websocket.send('ping');
    }
    timerId = setTimeout(keepAlive, timeout);
}

function cancelKeepAlive() {
    if (timerId) {
        clearTimeout(timerId);
    }
}

function loadMainPage() {
    //hide explore and profile
    $(".explorePage").hide();
    $(".profilePage").hide();
    $(".mainPage").show();
    renderChatroomOnStart(userId);
    $(function() {
        // Initializes and creates emoji set from sprite sheet
        window.emojiPicker = new EmojiPicker({
            emojiable_selector: '[data-emojiable=true]',
            assetsPath: 'http://onesignal.github.io/emoji-picker/lib/img/',
            popupButtonClasses: 'fa fa-smile-o'
        });
        // Finds all elements with `emojiable_selector` and converts them to rich emoji input fields
        // You may want to delay this step if you have dynamically created input fields that appear later in the loading process
        // It can be called as many times as necessary; previously converted input fields will not be converted again
        window.emojiPicker.discover();
    });
}

function loadExplore() {
    //hide main and profile
    curRoom = -1;
    $(".mainPage").hide();
    $(".profilePage").hide();
    $(".explorePage").show();
    loadAllRooms(userId);
}

function loadProfile() {
    //hide main and explore
    $(".mainPage").hide();
    $(".explorePage").hide();
    $(".profilePage").show();
    loadCurrentUserProfile(userId);
}