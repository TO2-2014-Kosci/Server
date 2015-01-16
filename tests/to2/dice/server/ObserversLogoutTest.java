package to2.dice.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.messaging.JoinRoomAction;
import to2.dice.messaging.Response;

import java.util.HashMap;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Janusz on 13-01-2015.
 */

@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(GameControllerFactory.class)
public class ObserversLogoutTest {

    @InjectMocks
    Server server = new Server();

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
        assertEquals("Join room correct player", responses[3].type, Response.Type.SUCCESS);

        responses[4] = server.logout("Janusz");
        assertEquals("Sign out with correct login", responses[4].type, Response.Type.SUCCESS);

        responses[5] = server.logout("Michal");
        assertEquals("Sign out with correct login", responses[5].type, Response.Type.SUCCESS);
    }
}