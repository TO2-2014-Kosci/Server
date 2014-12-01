package to2.dice.server;

import org.junit.Test;
import to2.dice.messaging.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Janusz on 01-12-2014.
 */
public class ServerTest {

    Server server = mock(Server.class);

    @Test
    public void testLogin() throws Exception {

        Response r1, r2, r3, r4 = mock(Response.class);

        r1 = server.login(null);
        r2 = server.login("Janusz");
        r3 = server.login("Januszek");
        r4 = server.login("Janusz");
    }
}
