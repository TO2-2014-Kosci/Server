package to2.dice.server;

import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;

import java.util.HashMap;

/**
 * Created by Janusz on 05-12-2014.
 */
public class BotsGameTest {
    HashMap<BotLevel, Integer> botsNumber;

    public void run() {
        botsNumber.put(BotLevel.HIGH, 0);
        botsNumber.put(BotLevel.HIGH, 1);
        botsNumber.put(BotLevel.LOW, 2);
        botsNumber.put(BotLevel.LOW, 3);
        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Slupsk", 1, 20, 3, 3, botsNumber);
    }
}