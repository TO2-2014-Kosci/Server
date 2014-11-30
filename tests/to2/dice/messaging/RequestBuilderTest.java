package to2.dice.messaging;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class RequestBuilderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidGetRoomList() throws Exception {
        thrown.expect(InvalidArgumentException.class);

        Request request = new RequestBuilder(Request.Type.GET_ROOM_LIST)
                .login("test_login").build();

        System.out.println("haj");
    }
}