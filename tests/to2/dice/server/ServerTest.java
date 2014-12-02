package to2.dice.server;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.GameType;
import to2.dice.messaging.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Janusz on 01-12-2014.
 */

public class ServerTest {

    Server server = new Server();

    @Test
    public void testLogin() throws Exception {
        Response [] responses = new Response[4];

        responses[0] = server.login(null);
        responses[1] = server.login("Janusz");
        responses[2] = server.login("Januszek");
        responses[3] = server.login("Janusz");

        assertEquals("Response responses[0] = server.login(null) has type SUCCESS", responses[0].type, Response.Type.SUCCESS);
        assertEquals("Response responses[1] = server.login(\"Janusz\") has type SUCCESS", responses[1].type, Response.Type.SUCCESS);
        assertEquals("Response responses[2] = server.login(\"Januszek\") has type SUCCESS", responses[2].type, Response.Type.SUCCESS);
        assertEquals("Response responses[3] = server.login(\"Janusz\") has type FAILURE", responses[3].type, Response.Type.FAILURE);
    }

    @Test
    public void testCreateRoom() throws Exception {
        String [] roomNames = new String [3];
        roomNames[0] = "Sosnowiec";
        roomNames[1] = "Gwozdziec";
        roomNames[2] = "Sosnowiec";

        GameSettings settingsMock = mock(GameSettings.class);
        when(settingsMock.getGameType()).thenReturn(GameType.POKER);

        String creator = "Alfons";

        Response [] responses = new Response[3];

        GameState gameState = new GameState();
        GameInfo gameInfo = new GameInfo(settingsMock, gameState);

        GameController gameControllerMock = mock(GameController.class);
        when(gameControllerMock.getGameInfo()).thenReturn(gameInfo);

        //PowerMockito.mockStatic(ClassWithStatics.class);
        //when(ClassWithStatics.getString()).thenReturn("Hello!");

        PowerMockito.mock(GameControllerFactory.class);
        //PowerMockito.when(GameControllerFactory.createGameControler(server, settingsMock, creator)).thenReturn(gameControllerMock);

        responses[0] = server.createRoom(roomNames[0], settingsMock, creator);
        responses[1] = server.createRoom(roomNames[1], settingsMock, creator);
        responses[2] = server.createRoom(roomNames[2], settingsMock, creator);
    }
}
