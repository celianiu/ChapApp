'use strict';

function validate(event) {
    event.preventDefault();
    let name = $("#name").val();
    let age = $("#age").val();
    let school = $("#school").val();
    let interests = $("#interests").val();
    let hasError = false;
    if (!validateSingleInput(name)) {
        $("#name").addClass("is-invalid");
        $("#name-feedback").show();
        hasError = true;
    }
    if (!validateSingleInput(age)) {
        $("#age").addClass("is-invalid");
        $("#age-feedback").show();
        hasError = true;
    }
    if (!validateSingleInput(school)) {
        $("#school").addClass("is-invalid");
        $("#school-feedback").show();
        hasError = true;
    }
    if (!validateSingleInput(interests)) {
        $("#interests").addClass("is-invalid");
        $("#interests-feedback").show();
        hasError = true;
    }
    if (!hasError) {
        $.post("/login", {
            username: name, age: age, interests: interests, school: school
        }, function (data) {
            localStorage.setItem("userId", data.loginUser.userId);
            $("#form").submit();
        }, "json");
    }
}

/**
 * validate field context
 * @param input
 * @return {boolean}, true means OK
 */
const validateSingleInput = (input) => {
    return !(input === "" || input === null);
}

let userId = null;
window.onload = function () {
    userId = localStorage.getItem("userId");
    //local storage have userId,then it will redirect to explore
    if (userId !== null) {
        location.href = "bigPage.html";
    }
    $("#btn-login").click(function () {
        validate(event);
    })
}