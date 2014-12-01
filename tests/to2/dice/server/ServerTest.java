package to2.dice.server;

import org.junit.Test;
import to2.dice.game.GameSettings;
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

        assertEquals("Response r1 = server.login(null) has type SUCCESS", responses[0].type, Response.Type.SUCCESS);
        assertEquals("Response r2 = server.login(\"Janusz\") has type SUCCESS", responses[1].type, Response.Type.SUCCESS);
        assertEquals("Response r3 = server.login(\"Januszek\") has type SUCCESS", responses[2].type, Response.Type.SUCCESS);
        assertEquals("Response r4 = server.login(\"Janusz\") has type FAILURE", responses[3].type, Response.Type.FAILURE);
    }

    @Test
    public void testCreateRoom() throws Exception {
        String [] roomNames = new String [3];
        roomNames[0] = "Sosnowiec";
        roomNames[1] = "Gwozdziec";
        roomNames[2] = "Sosnowiec";

        GameSettings roomSettings = mock (GameSettings.class);

        System.out.println(roomSettings.getGameType());

        String creator = "Alfons";
    }
}
