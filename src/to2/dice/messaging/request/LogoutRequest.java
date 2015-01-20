package to2.dice.messaging.request;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.2
 */
public class LogoutRequest extends LoginRequest {

    public LogoutRequest() {}
    public LogoutRequest(String login) {
        super(login);
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("logout_request", new JSONObject().put("login", login));
    }

    @Override
    public void fromJson(JSONObject json) {
        json = json.getJSONObject("logout_request");
        login = json.getString("login");
    }

    @Override
    public Type getType() {
        return Type.LOGOUT;
    }
}
