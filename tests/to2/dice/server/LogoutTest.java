package to2.dice.server;

import org.junit.Test;
import to2.dice.messaging.Response;
import static org.junit.Assert.*;

/**
 * Created by Janusz on 13-01-2015.
 */
public class LogoutTest {

    Server server = new Server();

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
}
