package to2.dice.server;

import org.junit.Test;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.*;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Janusz on 05-12-2014.
 */
public class BotsGameTest {

    final ArrayList<GameState> states = new ArrayList<GameState>();
    boolean hasEnded = false;

    @Test
    public void botGameTest() throws Exception {
        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();

        botsNumber.put(BotLevel.HIGH, 3);
        botsNumber.put(BotLevel.LOW, 8);
        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Slupsk", 0, 20, 3, 3, botsNumber);

        GameServer server = new GameServer() {
            @Override
            public void sendToAll(GameController sender, GameState state) {
                states.add(state);
            }

            @Override
            public void finishGame(GameController sender) {
                hasEnded = true;
            }
        };

        GameController gc = GameControllerFactory.createGameControler(server, gameSettings, "Ambrozy");

        while (!hasEnded) {
            synchronized (this) {
                wait();
            }
        }

        assertTrue("Game was longer than 2 rounds", states.size() > 2);

        ArrayList<Player> winners = new ArrayList<Player>();
        for (Player p : states.get(states.size() - 1).getPlayers())
            if (p.getScore() == 3)
                winners.add(p);

        assertEquals("There is exactly one winner", winners.size(), 1);
    }
}