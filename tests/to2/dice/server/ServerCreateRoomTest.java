package to2.dice.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.*;
import to2.dice.messaging.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(GameControllerFactory.class)
public class ServerCreateRoomTest {
    @InjectMocks
    Server server;

    @Test
    public void testCreateRoom() throws Exception {
        ControllerMockSet set;
        Response response;

        set = createSet("Sosnowiec");
        response = server.createRoom(set.gset, "Alfons");
        assertEquals("Create room with unique name", response.type, Response.Type.SUCCESS);

        set = createSet("Gwozdziec");
        response = server.createRoom(set.gset, "Alfons");
        assertEquals("Creating room with unique name", response.type, Response.Type.SUCCESS);

        set = createSet("Sosnowiec");
        response = server.createRoom(set.gset, "Alfons");
        assertEquals("Create room with not unique name", response.type, Response.Type.FAILURE);
    }

    class ControllerMockSet {
        public GameController gc;
        public GameSettings gset;
        public GameInfo gi;
        public GameState gstate;
    }

    private ControllerMockSet createSet(String roomName) {
        ControllerMockSet set = new ControllerMockSet();

        set.gset = mock(GameSettings.class);
        when(set.gset.getGameType()).thenReturn(GameType.POKER);
        when(set.gset.getName()).thenReturn(roomName);

        set.gstate = new GameState();
        set.gi = new GameInfo(set.gset, set.gstate);

        set.gc = mock(GameController.class);
        when(set.gc.getGameInfo()).thenReturn(set.gi);

        PowerMockito.mockStatic(GameControllerFactory.class);
        PowerMockito.when(GameControllerFactory.createGameControler(any(Server.class), eq(set.gset), any(String.class)))
                .thenReturn(set.gc);

        return set;
    }
}