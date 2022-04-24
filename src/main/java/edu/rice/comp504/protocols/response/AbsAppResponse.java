package edu.rice.comp504.protocols.response;


public abstract class AbsAppResponse {

    enum ResponseType {
        Message(1),   //Someone sends a new message
        NEW(2),    //Create a new room
        SWITCH(3),  // switch current room
        INVITED(4),  //invite other to this room
        KICKED(5),   //kick someone in the room
        LOGIN(6),    //login
        LEAVE(7);    //login

        private final int value;

        ResponseType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ResponseType responseType;

    abstract ResponseType getResponseType();
}
