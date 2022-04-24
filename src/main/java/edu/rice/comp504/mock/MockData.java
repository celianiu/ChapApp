package edu.rice.comp504.mock;

import edu.rice.comp504.model.message.PublicMessage;
import edu.rice.comp504.model.room.Room;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.user.UserDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockData {
    /**
     * Mock data for test.
     * @return mock room
     */
    public static Room mockRoom() {

        User.Builder ub = new User.Builder("Mack Joyner");
        List<String> interests = new ArrayList<>();
        interests.add("Coding");
        interests.add("Cooking");
        User mack = ub.age(20).school("Rice University").interests(interests).build();
        Room.Builder builder = new Room.Builder(mack);
        Room mockroom = builder.isPrivate(false).maxNumber(5).roomName("mock room").build();
        UserDB.addUser(mack.getUserId(),mack);

        ub = new User.Builder("Risa Myers");
        interests = new ArrayList<>();
        interests.add("Fishing");
        interests.add("Shopping");
        User risa = ub.age(30).school("Rice University").interests(interests).build();
        UserDB.addUser(risa.getUserId(),risa);
        mockroom.join(risa.getUserId());
//        PublicMessage message = new PublicMessage(1, risa.getUserId(), "Risa Myers",mock_room.getRoomID(), new Date().getTime(),
//                "你在干嘛","Everyone");
//        mock_room.getMessageHistory().add(message);
//        PublicMessage message2 = new PublicMessage(2, mack.getUserId(), "Mack Joyner",mock_room.getRoomID(), new Date().getTime() + 2000,
//                "我在吃饭","Everyone");
//        mock_room.getMessageHistory().add(message);
//        mock_room.getMessageHistory().add(message2);
        return mockroom;
    }
}
