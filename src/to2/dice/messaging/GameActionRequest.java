package to2.dice.messaging;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public class GameActionRequest extends LoginRequest {
    private to2.dice.messaging.GameAction action;

    public GameActionRequest(String login, to2.dice.messaging.GameAction action) {
        super(login);
        this.action = action;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    public to2.dice.messaging.GameAction getAction() {
        return action;
    }

    public void setAction(to2.dice.messaging.GameAction action) {
        this.action = action;
    }
}
