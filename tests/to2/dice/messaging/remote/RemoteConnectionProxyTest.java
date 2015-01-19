package to2.dice.messaging.remote;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.game.*;
import to2.dice.messaging.RemoteConnectionProxy;
import to2.dice.server.Server;
import to2.dice.server.ServerMessageListener;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RemoteConnectionProxyTest {
    Server server;
    RemoteConnectionProxy rcp;
    ServerMessageListener listener;
    String login = "TestLogin";
    GameSettings roomSettings;

    private class CollectorListener implements ServerMessageListener {
        public ArrayList<GameState> states = new ArrayList<GameState>();

        @Override
        public void onGameStateChange(GameState state) {
            states.add(state);
        }
    }

    @Before
    public void setUp() throws Exception {
        server = new Server();

        listener = new CollectorListener();

        rcp = new RemoteConnectionProxy("localhost", listener);
        rcp.login(login);
        roomSettings = new GameSettings(GameType.POKER, 5, "roomName", 5, 5, 5, 5, new HashMap<BotLevel, Integer>());
    }

    @After
    public void tearDown() throws Exception {
        server.close();
    }

    @Test
    public void testLogin() throws Exception {
        assertEquals("Sets local login", login, rcp.getLoggedInUser());
        assertFalse("Logs user in server", server.login(login).isSuccess());
    }

    @Test
    public void testGetRoomList() throws Exception {
        assertEquals("Gets empty room list", rcp.getRoomList().size(), 0);

        assertTrue("Creates room", rcp.createRoom(roomSettings).isSuccess());

        List<GameInfo> rooms = rcp.getRoomList();

        assertEquals("Returns one room in list", rooms.size(), 1);

        GameInfo info = rooms.get(0);
        assertEquals("Returns correct room", info.getSettings().getName(), roomSettings.getName());
        assertEquals("Returns correct room", info.getSettings().getBotsNumbers(), roomSettings.getBotsNumbers());
        assertEquals("Returns correct room", info.getSettings().getDiceNumber(), roomSettings.getDiceNumber());
        assertEquals("Returns correct room", info.getSettings().getRoundsToWin(), roomSettings.getRoundsToWin());
        assertEquals("Returns correct room", info.getSettings().getGameType(), roomSettings.getGameType());
    }

    @Test
    public void testConnect() throws Exception {
        RemoteConnectionProxy rcp = new RemoteConnectionProxy("localhost", null);
    }

    @Test
    public void testJoinRoom() throws Exception {
        assertTrue("Creates room 1 with unique name", rcp.createRoom(roomSettings).isSuccess());
        rcp.leaveRoom();
        assertTrue("Creates room 2 with unique name", rcp.createRoom(new GameSettings(GameType.POKER, 5, "room2Name", 5, 5, 5, 5, new HashMap<BotLevel, Integer>())).isSuccess());

        String player2 = "Player2";
        RemoteConnectionProxy player2proxy = new RemoteConnectionProxy("localhost", new CollectorListener());
        player2proxy.login(player2);
        assertTrue("Can join room", player2proxy.joinRoom("room2Name").isSuccess());
        assertFalse("Can't join room if already in another room", player2proxy.joinRoom("room2Name").isSuccess());
        assertFalse("Can't join room if already joined it", player2proxy.joinRoom("roomName").isSuccess());
    }

    @Test
    public void testCreateRoom() throws Exception {
        RemoteConnectionProxy rcp = new RemoteConnectionProxy("localhost", null);
        rcp.login("login");
        GameSettings roomSettings = new GameSettings(GameType.POKER, 5, "roomName", 5, 5, 5, 5, new HashMap<BotLevel, Integer>());

        assertTrue("Creates room with unique name", rcp.createRoom(roomSettings).isSuccess());
        assertFalse("Creates room with same name", rcp.createRoom(roomSettings).isSuccess());
    }

    @Test
    public void testSimpleGameTest() throws Exception {
        final String login = "JM";

        rcp = new RemoteConnectionProxy("localhost", new ServerMessageListener() {
            @Override
            public void onGameStateChange(GameState gameState) {
                if (!gameState.isGameStarted())
                     return;

                if (gameState.getCurrentPlayer() != null && gameState.getCurrentPlayer().getName().equals(login)) {
                    try {
                        rcp.reroll(new boolean[] { true, false, false, true, false });
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }

                String player;
                if (gameState.getCurrentPlayer() != null) {
                    player = gameState.getCurrentPlayer().getName();
                    String dice = new JSONArray(gameState.getCurrentPlayer().getDice().getDiceArray()).toString();
                    System.out.format("Ruch gracza %s, kości: %s%n", player, dice);
                }
            }
        });

        rcp.login(login);
        rcp.createRoom(new GameSettings(GameType.POKER, 5, "The Game", 1, 20, 0, 2, new HashMap<BotLevel, Integer>()));
        rcp.sitDown();

        Thread.sleep(2000);
    }
}
