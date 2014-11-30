package to2.dice.messaging;

import org.junit.Test;

import static org.junit.Assert.*;

public class CreateGameRequestTest {

    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testGetType() throws Exception {
        CreateGameRequest r = new CreateGameRequest(null, null);
        assertEquals("CreateGameRequest has type CREATE_GAME_REQUEST", r.getType(), Request.Type.CREATE_GAME);
    }
}