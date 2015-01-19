package to2.dice.server;

import org.junit.Before;
import org.junit.Test;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameType;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.messaging.JoinRoomAction;
import to2.dice.messaging.Response;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author Janusz
 * Tests Server functionality
 */
public class ServerTest {
    Server server;

    @Before
    public void setUp() {
        server = new Server();
    }

    @Test
    public void testLogin() throws Exception {
        Response[] responses = new Response[5];

        responses[0] = server.login(null);
        responses[1] = server.login("        ");
        responses[2] = server.login("Janusz");
        responses[3] = server.login("Januszek");
        responses[4] = server.login("Janusz");

        assertEquals("Signing in with null login", responses[0].type, Response.Type.FAILURE);
        assertEquals("Signing in with blank login", responses[1].type, Response.Type.FAILURE);
        assertEquals("Signing in with unique login", responses[2].type, Response.Type.SUCCESS);
        assertEquals("Signing in with unique login", responses[3].type, Response.Type.SUCCESS);
        assertEquals("Signing in with not unique login", responses[4].type, Response.Type.FAILURE);
    }

    @Test
    public void testLogout() throws Exception {
        Response[] responses = new Response[5];

        responses[0] = server.login("Janusz");
        responses[1] = server.logout(null);
        responses[2] = server.logout("        ");
        responses[3] = server.logout("Januszek");
        responses[4] = server.logout("Janusz");

        assertEquals("Signing in with correct login", responses[0].type, Response.Type.SUCCESS);
        assertEquals("Signing out with null login", responses[1].type, Response.Type.FAILURE);
        assertEquals("Signing out with blank login", responses[2].type, Response.Type.FAILURE);
        assertEquals("Signing out with not signed in login", responses[3].type, Response.Type.FAILURE);
        assertEquals("Signing out with correct login", responses[4].type, Response.Type.SUCCESS);
    }

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

    @Test
    public void testObserversLogout() throws Exception {
        Response[] responses = new Response[6];

        responses[0] = server.login("Janusz");
        assertEquals("Sign in with correct login", responses[0].type, Response.Type.SUCCESS);

        responses[1] = server.login("Michal");
        assertEquals("Sign in with correct login", responses[1].type, Response.Type.SUCCESS);

        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();

        GameSettings gameSettings = new GameSettings(GameType.POKER, 5, "Radom", 3, 20, 3, 2, botsNumber);
        responses[2] = server.createRoom(gameSettings, "Janusz");
        assertEquals("Create room with unique name", responses[2].type, Response.Type.SUCCESS);

        JoinRoomAction joinRoomAction = new JoinRoomAction("Michal", "Radom");
        responses[3] = server.handleGameAction(joinRoomAction);
        assertEquals("Join room Guest player", responses[3].type, Response.Type.SUCCESS);

        responses[4] = server.logout("Janusz");
        assertEquals("Sign out Owner player", responses[4].type, Response.Type.SUCCESS);

        responses[5] = server.logout("Michal");
        assertEquals("Sign out Guest player", responses[5].type, Response.Type.SUCCESS);
    }
}
