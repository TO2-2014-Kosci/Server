package to2.dice.messaging;

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

    public CreateGameRequest(String login, GameSettings settings) {
        super(login);
        this.settings = settings;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject("create_game")
                .put("settings", serializeSettings());
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

    private JSONObject serializeSettings() {
        JSONObject settingsObject = new JSONObject();
        settingsObject.put("room_name", settings.getName())
                .put("game_type", settings.getGameType().name())
                .put("dice_num", settings.getDiceNumber())
                .put("max_players", settings.getMaxHumanPlayers())
                .put("max_inactive", settings.getMaxInactiveTurns())
                .put("rounds", settings.getRoundsToWin())
                .put("turn_time", settings.getTimeForMove());

        JSONArray bots = new JSONArray();
        for (BotLevel l : BotLevel.values()) {
            Integer n = settings.getBotsNumbers().get(l);
            if (n != null && n != 0)
                bots.put(new JSONObject().put(l.name(), n.intValue()));
        }

        settingsObject.put("bots", bots);

        return settingsObject;
    }
}
