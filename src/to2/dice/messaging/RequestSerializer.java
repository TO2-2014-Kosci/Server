package to2.dice.messaging;

import org.json.JSONArray;
import org.json.JSONObject;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fan
 * @version 0.1
 */
public final class RequestSerializer {
    public static JSONObject serializeSettings(GameSettings settings)  {
        JSONObject settingsObject = new JSONObject();
        settingsObject.put("room_name", settings.getName())
                .put("game_type", settings.getGameType().name())
                .put("dice_num", settings.getDiceNumber())
                .put("max_players", settings.getMaxHumanPlayers())
                .put("max_inactive", settings.getMaxInactiveTurns())
                .put("rounds", settings.getRoundsToWin())
                .put("turn_time", settings.getTimeForMove());

        settingsObject.put("bots", serializeBots(settings.getBotsNumbers()));

        return settingsObject;
    }

    public static GameSettings deserializeSettings(JSONObject settingsObject) {
        JSONObject bots = settingsObject.has("bots") ? settingsObject.getJSONObject("bots") : null;
        return new GameSettings(
                GameType.valueOf(settingsObject.getString("game_type")),
                settingsObject.getInt("dice_num"),
                settingsObject.getString("room_name"),
                settingsObject.getInt("max_players"),
                settingsObject.getInt("turn_time"),
                settingsObject.getInt("max_inactive"),
                settingsObject.getInt("rounds"),
                deserializeBots(bots)
        );
    }

    public static JSONObject serializeBots(Map<BotLevel, Integer> bots) {
        if (bots == null) return null;

        JSONObject botArray = new JSONObject();
        for (BotLevel l : BotLevel.values()) {
            Integer n = bots.get(l);
            if (n != null && n != 0)
                botArray.put(l.name(), n.intValue());
        }
        return botArray;
    }

    public static Map<BotLevel, Integer> deserializeBots(JSONObject botsArray) {
        if (botsArray == null) return null;

        Map<BotLevel, Integer> bots = new HashMap<BotLevel, Integer>();
        for (BotLevel l : BotLevel.values()) {
            if (botsArray.has(l.name()))
                bots.put(l, botsArray.getInt(l.name()));
        }

        return bots;
    }
}
