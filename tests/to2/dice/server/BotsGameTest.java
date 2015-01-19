package to2.dice.server;

import org.junit.Test;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author Janusz
 * Test game where only bots play
 */
public class BotsGameTest {

    final ArrayList<GameState> states = new ArrayList<GameState>();
    boolean hasEnded = false;

    @Test
    public void botGameTest() throws Exception {
        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();

        botsNumber.put(BotLevel.HARD, 1);
        botsNumber.put(BotLevel.EASY, 3);
        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Slupsk", 0, 20, 3, 2, botsNumber);

        GameServer server = new GameServer() {
            private int round = -1;
            boolean started = false;

            @Override
            public void sendToAll(GameController sender, GameState state) {
                if (round != state.getCurrentRound()) {
                    round = state.getCurrentRound();
                    System.out.printf("%nRozpoczęto rundę %d ", round);
                }
                states.add(state);
                System.out.print(".");

                started = started || state.isGameStarted();
                if (started && !state.isGameStarted())
                    sender.handleGameAction(new GameAction(GameActionType.LEAVE_ROOM, "Ambrozy"));
            }

            @Override
            public void finishGame(GameController sender) {
                hasEnded = true;
                System.out.printf("%nZakończono grę%n");
            }
        };

        GameController gc = GameControllerFactory.createGameControler(server, gameSettings, "Ambrozy");

        while (!hasEnded) {
            synchronized (this) {
                wait(100);
            }
        }

        assertTrue("Game was longer than 2 rounds", states.size() > 2);

        ArrayList<Player> winners = new ArrayList<Player>();
        for (Player p : states.get(states.size() - 1).getPlayers())
            if (p.getScore() == 2)
                winners.add(p);

        assertEquals("There is exactly one winner", 1, winners.size());
    }
}
