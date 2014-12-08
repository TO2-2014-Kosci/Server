package to2.dice.server;

import org.junit.Test;
import org.mockito.InjectMocks;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;
import to2.dice.messaging.Response;

import java.util.HashMap;

/**
 * Created by Janusz on 05-12-2014.
 */
public class BotsGameTest {
    HashMap<BotLevel, Integer> botsNumber;

    @InjectMocks
    Server server;

    @Test
    public void run() throws Exception {
        botsNumber = new HashMap<BotLevel, Integer>();

        botsNumber.put(BotLevel.HIGH, 0);
        botsNumber.put(BotLevel.HIGH, 1);
        botsNumber.put(BotLevel.LOW, 2);
        botsNumber.put(BotLevel.LOW, 3);
        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Slupsk", 1, 20, 3, 3, botsNumber);

        Response responseLogin = server.login("Ambrozy"); // <-NullPointerException
//drugiego nie pokaze :p
    }
}