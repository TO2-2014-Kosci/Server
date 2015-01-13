package to2.dice.messaging.request;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public class GetRoomListRequest extends Request {
    @Override
    public JSONObject toJson() {
        return new JSONObject().put("get_room_list", "t");
    }

    @Override
    public void fromJson(JSONObject json) {
        if (!json.has("get_room_list"))
            throw new IllegalArgumentException("Is not valid request");
    }

    @Override
    public Type getType() {
        return Type.GET_ROOM_LIST;
    }
}
