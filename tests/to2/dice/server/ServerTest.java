package to2.dice.server;

import org.junit.Test;
import to2.dice.messaging.Response;

import static org.junit.Assert.*;

/**
 * Created by Janusz on 01-12-2014.
 */
public class ServerTest {

    Server server = new Server();

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
}
