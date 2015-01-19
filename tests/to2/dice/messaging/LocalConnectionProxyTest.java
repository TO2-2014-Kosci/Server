package to2.dice.messaging;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.game.*;
import to2.dice.server.Server;
import to2.dice.server.ServerMessageListener;

import java.util.*;

import static org.junit.Assert.*;

public class LocalConnectionProxyTest {

    Server server;
    LocalConnectionProxy lcp;
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

        lcp = new LocalConnectionProxy(server, listener);
        lcp.login(login);
        roomSettings = new GameSettings(GameType.POKER, 5, "roomName", 5, 5, 5, 5, new HashMap<BotLevel, Integer>());
    }

    @After
    public void tearDown() throws Exception {
        server.close();
    }

    @Test
    public void testLogin() throws Exception {
        assertEquals("Sets local login", login, lcp.getLoggedInUser());
        assertFalse("Logs user in server", server.login(login).isSuccess());
    }

    @Test
    public void testGetRoomList() throws Exception {
        assertEquals("Gets empty room list", lcp.getRoomList().size(), 0);

        assertTrue("Creates room", lcp.createRoom(roomSettings).isSuccess());

        List<GameInfo> rooms = lcp.getRoomList();

        assertEquals("Returns one room in list", rooms.size(), 1);
        assertTrue("Returns correct room", rooms.get(0).getSettings().equals(roomSettings));
    }

    @Test
    public void testCreateRoom() throws Exception {
        assertTrue("Creates room with unique name", lcp.createRoom(roomSettings).isSuccess());
        assertFalse("Creates room with same name", lcp.createRoom(roomSettings).isSuccess());
    }

    @Test
    public void testJoinRoom() throws Exception {
        assertTrue("Creates room 1 with unique name", lcp.createRoom(roomSettings).isSuccess());
        assertTrue("Creates room 2 with unique name", lcp.createRoom(new GameSettings(GameType.POKER, 5, "room2Name", 5, 5, 5, 5, new HashMap<BotLevel, Integer>())).isSuccess());

        String player2 = "Player2";
        LocalConnectionProxy player2proxy = new LocalConnectionProxy(server, new CollectorListener());
        player2proxy.login(player2);
        assertTrue("Can join room", player2proxy.joinRoom("roomName").isSuccess());
        assertFalse("Can't join room if already in another room", player2proxy.joinRoom("room2Name").isSuccess());
        assertFalse("Can't join room if already joined it", player2proxy.joinRoom("roomName").isSuccess());
    }

    @Test
    public void testLeaveRoom() throws Exception {
        assertTrue("Creates room", lcp.createRoom(roomSettings).isSuccess());


    }

    @Test
    public void testSimpleGameTest() throws Exception {
        final String login = "JM";

        lcp = new LocalConnectionProxy(server, new ServerMessageListener() {
            @Override
            public void onGameStateChange(GameState gameState) {
                if (!gameState.isGameStarted())
                    return;

                if (gameState.getCurrentPlayer() != null && gameState.getCurrentPlayer().getName().equals(login)) {
                    lcp.reroll(new boolean[] { true, false, false, true, false });
                }

                String player;
                if (gameState.getCurrentPlayer() != null) {
                    player = gameState.getCurrentPlayer().getName();
                    String dice = new JSONArray(gameState.getCurrentPlayer().getDice().getDiceArray()).toString();
                    System.out.format("Ruch gracza %s, kości: %s%n", player, dice);
                }
            }
        });

        lcp.login(login);
        lcp.createRoom(new GameSettings(GameType.POKER, 5, "The Game", 1, 20, 0, 2, new HashMap<BotLevel, Integer>()));
        lcp.sitDown();

        Thread.sleep(2000);
    }
}