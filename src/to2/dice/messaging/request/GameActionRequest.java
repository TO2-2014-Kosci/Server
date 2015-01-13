package to2.dice.messaging.request;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public class GameActionRequest extends LoginRequest {
    protected to2.dice.messaging.GameAction action;

    public GameActionRequest() {}
    public GameActionRequest(String login, to2.dice.messaging.GameAction action) {
        super(login);
        this.action = action;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("game_action", new JSONObject()
                .put("action", RequestSerializer.serializeGameAction(action))
                .put("login", login));
    }

    @Override
    public void fromJson(JSONObject json) {
        json = json.getJSONObject("game_action");
        action = RequestSerializer.deserializeGameAction(json.getJSONObject("action"));
        login = json.getString("login");
    }

    @Override
    public Type getType() {
        return Type.GAME_ACTION;
    }

    public to2.dice.messaging.GameAction getAction() {
        return action;
    }

    public void setAction(to2.dice.messaging.GameAction action) {
        this.action = action;
    }
}
