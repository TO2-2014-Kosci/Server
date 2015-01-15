package to2.dice.messaging.request;

import org.json.JSONArray;
import org.json.JSONObject;
import to2.dice.game.*;
import to2.dice.messaging.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2015-01-13.
 */
public final class ResponseSerializer {
    public static JSONObject serializeResponse(Response response) {
        return new JSONObject().put("response", new JSONObject()
                .put("status", response.isSuccess())
                .put("message", response.message));
    }

    public static Response deserializeResponse(JSONObject json) {
        Response response = new Response();
        json = json.getJSONObject("response");

        if (json.getBoolean("status"))
            response.type = Response.Type.SUCCESS;
        else
            response.type = Response.Type.FAILURE;

        if (json.has("message"))
            response.message = json.getString("message");

        return response;
    }

    public static JSONObject serializeRoomList(List<GameInfo> roomList) {
        JSONArray rooms = new JSONArray();
        for (GameInfo info : roomList)
            rooms.put(new JSONObject()
                    .put("settings", RequestSerializer.serializeSettings(info.getSettings()))
                    .put("game_started", info.isGameStarted())
                    .put("players_number", info.getPlayersNumber()));

        return new JSONObject().put("room_list", rooms);
    }

    public static ArrayList<GameInfo> deserializeRoomList(JSONObject json) {
        JSONArray rooms = json.getJSONArray("room_list");
        ArrayList<GameInfo> roomList = new ArrayList<GameInfo>(rooms.length());

        GameInfo gameInfo;
        JSONObject info;
        for (int i = 0; i < rooms.length(); i++) {
            info = rooms.getJSONObject(i);
            gameInfo = new GameInfo(RequestSerializer.deserializeSettings(info.getJSONObject("settings")),
                    info.getBoolean("game_started"),
                    info.getInt("players_number"));
            roomList.add(gameInfo);
        }

        return roomList;
    }

    public static JSONObject serializeGameState(GameState state) {
        JSONArray players = new JSONArray();
        for (Player player : state.getPlayers())
            players.put(new JSONObject()
                    .put("name", player.getName())
                    .put("score", player.getScore())
                    .put("dice", new JSONArray(player.getDice().getDiceArray()))
                    .put("is_bot", player.isBot()));

        String current_player = null;
        if (state.getCurrentPlayer() != null)
            current_player = state.getCurrentPlayer().getName();

        JSONObject json = new JSONObject()
                .put("players", players)
                .put("game_started", state.isGameStarted())
                .put("current_player", current_player)
                .put("round", state.getCurrentRound());

        if (state instanceof NGameState)
            json.put("winning_number", ((NGameState)state).getWinningNumber());

        return new JSONObject().put("game_state", json);
    }

    public static GameState deserializeGameState(JSONObject json) {
        json = json.getJSONObject("game_state");

        GameState state;
        if (json.has("winning_number")) {
            state = new NGameState();
            ((NGameState)state).setWinningNumber(json.getInt("winning_number"));
        }
        else
            state = new GameState();

        JSONArray jsonPlayers = json.getJSONArray("players");
        ArrayList<Player> players = new ArrayList<Player>(jsonPlayers.length());

        String current_player = null;
        if (json.has("current_player"))
            current_player = json.getString("current_player");

        for (int i = 0; i < jsonPlayers.length(); i++) {
            JSONObject jsonPlayer = jsonPlayers.getJSONObject(i);
            JSONArray jsonDice = jsonPlayer.getJSONArray("dice");

            Player p = new Player(
                    jsonPlayer.getString("name"),
                    jsonPlayer.getBoolean("is_bot"),
                    jsonDice.length());

            int[] dice = new int[jsonDice.length()];
            for (int j = 0; j < jsonDice.length(); j++)
                dice[j] = jsonDice.getInt(j);

            p.setDice(new Dice(dice));
            p.setScore(jsonPlayer.getInt("score"));
            players.add(i, p);

            if (current_player != null && p.getName().equals(current_player))
                state.setCurrentPlayer(p);
        }

        state.setPlayers(players);
        state.setGameStarted(json.getBoolean("game_started"));
        state.setCurrentRound(json.getInt("round"));

        return state;
    }
}
