package to2.dice.messaging;

import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.server.Server;
import to2.dice.server.ServerMessageListener;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Fan on 2014-12-19.
 */
public class LocalConnectionProxy extends AbstractConnectionProxy {

    private Server server;

    public LocalConnectionProxy(Object serverLink, ServerMessageListener listener) throws ConnectException {
        super(serverLink, listener);
    }

    @Override
    public Response login(String login) {
        this.loggedInUser = login;
        return server.login(login);
    }

    @Override
    public Response logout() throws TimeoutException {
        String login = this.loggedInUser;
        this.loggedInUser = null;
        return server.logout(login);
    }

    @Override
    public List<GameInfo> getRoomList() {
        return server.getRoomList();
    }

    @Override
    public Response createRoom(GameSettings settings) {
        return server.createRoom(settings, this.loggedInUser);
    }

    @Override
    public Response joinRoom(String roomName) {
        try {
            return super.joinRoom(roomName);
        }
        catch(TimeoutException e) {
            return new Response(Response.Type.FAILURE, "Unknown error");
        }
    }

    @Override
    public Response leaveRoom() {
        try {
            return super.leaveRoom();
        }
        catch(TimeoutException e) {
            return new Response(Response.Type.FAILURE, "Unknown error");
        }
    }

    @Override
    public Response sitDown() {
        try {
            return super.sitDown();
        }
        catch(TimeoutException e) {
            return new Response(Response.Type.FAILURE, "Unknown error");
        }
    }

    @Override
    public Response standUp() {
        try {
            return super.standUp();
        }
        catch(TimeoutException e) {
            return new Response(Response.Type.FAILURE, "Unknown error");
        }
    }

    @Override
    public Response reroll(boolean[] dice) {
        try {
            return super.reroll(dice);
        }
        catch(TimeoutException e) {
            return new Response(Response.Type.FAILURE, "Unknown error");
        }
    }

    @Override
    protected void register() {
        server.registerLocalProxy(this);
    }

    @Override
    protected boolean connect(Object serverLink) {
        server = (Server) serverLink;
        if (server == null)
            return false;

        return true;
    }

    @Override
    protected Response sendGameAction(GameAction action) {
        return server.handleGameAction(action);
    }

    /**
     * for debugging purposes
     * @param login login to set
     */
    public void setLoggedInUser(String login) {
        this.loggedInUser = login;
    }
}
