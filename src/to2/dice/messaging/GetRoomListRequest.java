package to2.dice.messaging;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public class GetRoomListRequest extends Request {
    @Override
    public JSONObject toJson() {
        return new JSONObject("get_room_list");
    }

    @Override
    public Type getType() {
        return Type.GET_ROOM_LIST;
    }
}
