package to2.dice.server;

import org.junit.Test;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;
import to2.dice.messaging.Response;

import java.util.HashMap;

/**
 * Created by Janusz on 05-12-2014.
 */
public class BotsGameTest {

    @Test
    public void botGameTest() throws Exception {
        HashMap<BotLevel, Integer> botsNumber;
        Server server = new Server();
        botsNumber = new HashMap<BotLevel, Integer>();

        botsNumber.put(BotLevel.HIGH, 3);
        botsNumber.put(BotLevel.LOW, 8);
        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Slupsk", 1, 20, 3, 3, botsNumber);

        Response responseLogin = server.login("Ambrozy");
        Response responseCreateRoom = server.createRoom(gameSettings, "Ambrozy");
    }
}