package to2.dice.messaging;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import to2.dice.game.GameSettings;
import to2.dice.messaging.request.Request;
import to2.dice.messaging.request.RequestBuilder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RequestBuilderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidGetRoomList() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        Request request = new RequestBuilder(Request.Type.GET_ROOM_LIST)
                .login("test_login").build();
    }

    @Test
    public void testInvalidGameAction() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        Request request = new RequestBuilder()
                .login("test_login")
                .settings(mock(GameSettings.class))
                .type(Request.Type.GAME_ACTION)
                .build();
    }

    @Test
    public void testGetRoomListTypeValidation() throws Exception {
        Request request = new RequestBuilder().build();
        assertEquals("Request with no parameters has type GetRoomList",
                Request.Type.GET_ROOM_LIST, request.getType());
    }

    @Test
    public void testLoginTypeValidation() throws Exception {
        Request request = new RequestBuilder().login("test_login").build();
        assertEquals("Request with login parameter has type Login",
                Request.Type.LOGIN, request.getType());
    }

    @Test
    public void testGameActionTypeValidation() throws Exception {
        Request request = new RequestBuilder().login("test_login").action(mock(GameAction.class)).build();
        assertEquals("Request with login and action parameters has type GameAction",
                Request.Type.GAME_ACTION, request.getType());
    }

    @Test
    public void testCreateGameTypeValidation() throws Exception {
        Request request = new RequestBuilder().login("test_login").settings(mock(GameSettings.class)).build();
        assertEquals("Request with login and settings parameters has type CreateGame",
                Request.Type.CREATE_GAME, request.getType());
    }

    @Test
    public void testInvalidTypeValidation() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        Request request = new RequestBuilder()
                .login("test_login")
                .settings(mock(GameSettings.class))
                .action(mock(GameAction.class)).build();

    }
}