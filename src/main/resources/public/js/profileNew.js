
function parseDateWithSpace(data) {
    return data.replace("+", " ");
}

function loadCurrentUserProfile(userId) {
    $.post("/profile", {userid: userId}, function (data) {
        if (data === null) {
            return;
        }
        console.log("user profile is", data);
        //update profile.html
        $("#user-name").text(parseDateWithSpace(data.currUser.userName));
        $("#user-age").text(data.currUser.age);
        $("#user-school").text(parseDateWithSpace(data.currUser.school));
        let userInterests = "";
        data.currUser.interests.forEach((interest) => {
            userInterests += interest;
            userInterests += "   ";
        });
        $("#user-interests").text(parseDateWithSpace(userInterests));
    }, "json");
}