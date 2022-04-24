package edu.rice.comp504.protocols.request;


/**
 * someone sends a message to a chat room.
 */
public class RoomMessageRequest extends AbsAppRequest {
    int senderId;
    int roomId;
    String message;
    boolean isPrivate;
    int receiverId;

    @Override
    RequestType getRequestType() {
        return RequestType.MESSAGE;
    }
}
