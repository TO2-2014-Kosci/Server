package to2.dice.messaging.request;

import org.json.JSONArray;
import org.json.JSONObject;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;

/**
 * @author Fan
 * @version 0.1
 */
public class CreateGameRequest extends LoginRequest {
    private GameSettings settings;

    public CreateGameRequest() {}
    public CreateGameRequest(String login, GameSettings settings) {
        super(login);
        this.settings = settings;
    }

    @Override
    public JSONObject toJson() {
        if (login == null || settings == null)
            throw new NullPointerException("Neither login nor settings can be null");

        return new JSONObject().put("create_game", new JSONObject()
                .put("login", login)
                .put("settings", RequestSerializer.serializeSettings(settings)));
    }

    @Override
    public void fromJson(JSONObject json) {
        json = json.getJSONObject("create_game");
        login = json.getString("login");
        settings = RequestSerializer.deserializeSettings(json.getJSONObject("settings"));
    }

    @Override
    public Type getType() {
        return Type.CREATE_GAME;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }
}
