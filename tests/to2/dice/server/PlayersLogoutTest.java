package to2.dice.server;

import org.junit.Test;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.messaging.JoinRoomAction;
import to2.dice.messaging.Response;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Janusz on 13-01-2015.
 */
public class PlayersLogoutTest {

    Server server = new Server();
    @Test
    public void testPlayersLogout() throws Exception {
        Response[] responses = new Response[8];

        responses[0] = server.login("Janusz");
        assertEquals("Sign in with correct login", responses[0].type, Response.Type.SUCCESS);

        responses[1] = server.login("Michal");
        assertEquals("Sign in with correct login", responses[1].type, Response.Type.SUCCESS);

        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();

        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Sosnowiec", 3, 20, 3, 2, botsNumber);

        responses[2] = server.createRoom(gameSettings, "Janusz");
        assertEquals("Create room with unique name", responses[2].type, Response.Type.SUCCESS);

        JoinRoomAction joinRoomAction = new JoinRoomAction("Michal", "Sosnowiec");
        responses[3] = server.handleGameAction(joinRoomAction);
        assertEquals("Join room Guest player", responses[3].type, Response.Type.SUCCESS);

        GameAction sitDownOwner = new GameAction(GameActionType.SIT_DOWN, "Janusz");
        responses[4] = server.handleGameAction(sitDownOwner);
        assertEquals("Sit down Owner player", responses[4].type, Response.Type.SUCCESS);

        GameAction sitDownGuest = new GameAction(GameActionType.SIT_DOWN, "Michal");
        responses[5] = server.handleGameAction(sitDownGuest);
        assertEquals("Sit down Guest player", responses[5].type, Response.Type.SUCCESS);

        responses[6] = server.logout("Janusz");
        assertEquals("Sign out Owner player", responses[6].type, Response.Type.SUCCESS);

        responses[7] = server.logout("Michal");
        assertEquals("Sign out Guest player", responses[7].type, Response.Type.SUCCESS);
    }
}