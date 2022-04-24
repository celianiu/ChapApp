logOut = () => {
    localStorage.removeItem("userId");
    websocket.close();
}