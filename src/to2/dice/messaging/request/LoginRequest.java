package to2.dice.messaging.request;

import org.json.JSONObject;

/**
 * @author Fan
 * @version 0.1
 */
public class LoginRequest extends Request {
    protected String login;

    public LoginRequest(String login) {
        this.login = login;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject("login_request")
                .put("login", login);
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
