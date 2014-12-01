package to2.dice.server;

import org.junit.Test;
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
        Response r1, r2, r3, r4;

        r1 = server.login(null);
        r2 = server.login("Janusz");
        r3 = server.login("Januszek");
        r4 = server.login("Janusz");

        assertEquals("Response r1 = server.login(null) has type SUCCESS", r1.type, Response.Type.SUCCESS);
        assertEquals("Response r2 = server.login(\"Janusz\") has type SUCCESS", r2.type, Response.Type.SUCCESS);
        assertEquals("Response r3 = server.login(\"Januszek\") has type SUCCESS", r3.type, Response.Type.SUCCESS);
        assertEquals("Response r4 = server.login(\"Janusz\") has type FAILURE", r4.type, Response.Type.FAILURE);

    }
}
