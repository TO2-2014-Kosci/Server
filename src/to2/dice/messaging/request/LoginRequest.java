package to2.dice.messaging.request;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.2
 */
public class LoginRequest extends Request {
    protected String login;

    public LoginRequest() {}
    public LoginRequest(String login) {
        this.login = login;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("login_request", new JSONObject().put("login", login));
    }

    @Override
    public void fromJson(JSONObject json) {
        json = json.getJSONObject("login_request");
        login = json.getString("login");
    }

    @Override
    public Type getType() {
        return Type.LOGIN;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
