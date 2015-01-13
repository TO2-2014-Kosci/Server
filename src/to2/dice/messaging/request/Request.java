package to2.dice.messaging.request;

import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Fan
 * @version 0.1
 */
public abstract class Request {
    public enum Type {
        LOGIN, CREATE_GAME, GAME_ACTION, GET_ROOM_LIST
    }

    public Request() {}

    public static Request parse(String json) {
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.keySet().size() != 1)
            return null;

        String type = (String)jsonObject.keys().next();
        Request request;
        if (type.equals("login_request"))
            request = new LoginRequest();
        else if (type.equals("get_room_list"))
            request = new GetRoomListRequest();
        else if (type.equals("create_game"))
            request = new CreateGameRequest();
        else
            request = new GameActionRequest();

        request.fromJson(jsonObject);
        return request;
    }


    public abstract JSONObject toJson();

    public abstract void fromJson(JSONObject json);

    public abstract Type getType();

    @Override
    public String toString() {
        return toJson().toString();
    }
}
