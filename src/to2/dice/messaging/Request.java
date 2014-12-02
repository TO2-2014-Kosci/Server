package to2.dice.messaging;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public abstract class Request {
    public enum Type {
        LOGIN, CREATE_GAME, GAME_ACTION, GET_ROOM_LIST
    }

    public static Request parse(String json) {
        JSONObject jsonObject = new JSONObject(json);
        //TODO add deserializing logic
        return null;
    }


    public abstract JSONObject toJson();

    public abstract Type getType();

}
