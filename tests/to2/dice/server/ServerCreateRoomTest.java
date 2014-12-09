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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        server.login("Alfons");

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

    @Test
    public void testGetRoomList() throws Exception {
        ControllerMockSet[] sets = new ControllerMockSet[3];

        sets[0] = createSet("Sosnowiec");
        server.createRoom(sets[0].gset, "Alfons");

        sets[1] = createSet("Tarzymiechy Srednie");
        server.createRoom(sets[1].gset, "Alfons");

        sets[2] = createSet("Gwozdziec");
        server.createRoom(sets[2].gset, "Alfons");

        List<GameInfo> rooms = server.getRoomList();

        assertTrue("Show all rooms", rooms.contains(sets[0].gi));
        assertTrue("Show all rooms", rooms.contains(sets[1].gi));
        assertTrue("Show all rooms", rooms.contains(sets[2].gi));
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