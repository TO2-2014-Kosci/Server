package to2.dice.messaging;

import org.json.JSONObject;
import org.junit.Test;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;
import to2.dice.messaging.request.RequestSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestSerializerTest {

    @Test
    public void testSerializeBots() throws Exception {
        Map<BotLevel, Integer> bots = new HashMap<BotLevel, Integer>();
        bots.put(BotLevel.HARD, 5);
        bots.put(BotLevel.EASY, 0);

        String json = RequestSerializer.serializeBots(bots).toString();

        assertEquals("Creates proper json string",
                "{\"HIGH\":5}", json);
    }

    @Test
    public void testDeserializeBots() throws Exception {
        String json = "{\"HIGH\":3,\"LOW\":8}";
        Map<BotLevel, Integer> bots = RequestSerializer.deserializeBots(new JSONObject(json));

        assertEquals("Has accurate number of difficult bots",
                (int) bots.get(BotLevel.HARD), 3);
        assertEquals("Has accurate number of easy bots",
                (int) bots.get(BotLevel.EASY), 8);
    }

    @Test
    public void testSerializeSettings() throws Exception {
        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "testRoom", 3, 1, 0, 5, null);
        String json = RequestSerializer.serializeSettings(settings).toString();

        assertEquals("Creates proper json string",
                "{\"room_name\":\"testRoom\",\"dice_num\":5,\"max_inactive\":0,\"turn_time\":1,\"game_type\":\"NPLUS\",\"max_players\":3,\"rounds\":5}", json);
    }

    @Test
    public void testDeserializeSettings() throws Exception {
        String json = "{\"room_name\":\"testing\",\"max_inactive\":3,\"dice_num\":4,\"turn_time\":10,\"game_type\":\"POKER\",\"max_players\":12,\"rounds\":10}";
        GameSettings settings = RequestSerializer.deserializeSettings(new JSONObject(json));

        assertEquals("Has accurate name",
                settings.getName(), "testing");
        assertEquals("Has accurate maximum number of inactive turns",
                settings.getMaxInactiveTurns(), 3);
        assertEquals("Has accurate number of dice",
                settings.getDiceNumber(), 4);
        assertEquals("Has accurate time for move",
                settings.getTimeForMove(), 10);
        assertEquals("Has accurate game type",
                settings.getGameType(), GameType.POKER);
        assertEquals("Has accurate maximum number of players",
                settings.getMaxHumanPlayers(), 12);
        assertEquals("Has accurate number of rounds to win",
                settings.getRoundsToWin(), 10);
        assertNull("Has no bots", settings.getBotsNumbers());
    }
}