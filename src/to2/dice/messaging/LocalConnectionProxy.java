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
    public Response login(String login) throws TimeoutException {
        this.loggedInUser = login;
        return server.login(login);
    }

    @Override
    public List<GameInfo> getRoomList() throws TimeoutException {
        return server.getRoomList();
    }

    @Override
    public Response createRoom(GameSettings settings) throws TimeoutException {
        return server.createRoom(settings, this.loggedInUser);
    }

    @Override
    protected boolean connect(Object serverLink) {
        server = (Server) serverLink;
        if (server == null)
            return false;

        server.registerLocalProxy(this);
        return true;
    }

    @Override
    protected Response sendGameAction(GameAction action) {
        return server.handleGameAction(action);
    }

    public void sendState(GameState s) {
        for (ServerMessageListener sml : listeners)
            sml.onGameStateChange(s);
    }
}
