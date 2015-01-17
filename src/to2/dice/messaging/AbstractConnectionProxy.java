package to2.dice.messaging;

import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.server.ConnectionProxy;
import to2.dice.server.ServerMessageListener;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Fan
 * @version 0.1
 */
public abstract class AbstractConnectionProxy implements ConnectionProxy {
    protected List<ServerMessageListener> listeners;
    protected String loggedInUser;

    public AbstractConnectionProxy(Object serverLink, ServerMessageListener listener) throws ConnectException {
        listeners = new ArrayList<ServerMessageListener>();
        if (!connect(serverLink))
            throw new ConnectException("Could not connect to the server");

        register();
        addServerMessageListener(listener);
    }

    @Override
    public abstract Response login(String login) throws TimeoutException;

    @Override
    public abstract Response logout() throws TimeoutException;

    @Override
    public abstract List<GameInfo> getRoomList() throws TimeoutException;

    @Override
    public abstract Response createRoom(GameSettings settings) throws TimeoutException;

    @Override
    public Response joinRoom(String roomName) throws TimeoutException {
        GameAction action = new JoinRoomAction(this.loggedInUser, roomName);
        return sendGameAction(action);
    }

    @Override
    public Response leaveRoom() throws TimeoutException {
        GameAction action = new GameAction(GameActionType.LEAVE_ROOM, this.loggedInUser);
        return sendGameAction(action);
    }

    @Override
    public Response sitDown() throws TimeoutException {
        GameAction action = new GameAction(GameActionType.SIT_DOWN, this.loggedInUser);
        return sendGameAction(action);
    }

    @Override
    public Response standUp() throws TimeoutException {
        GameAction action = new GameAction(GameActionType.STAND_UP, this.loggedInUser);
        return sendGameAction(action);
    }

    @Override
    public Response reroll(boolean[] dice) throws TimeoutException {
        GameAction action = new RerollAction(this.loggedInUser, dice);
        return sendGameAction(action);
    }

    @Override
    public void addServerMessageListener(ServerMessageListener listener) {
        if (listener != null)
            this.listeners.add(listener);
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Registers ConnectionProxy in the Server
     */
    protected void register() {}

    /**
     * Connects to the server using serverLink
     * @param serverLink object representing server specific to the ConnectionProxy type
     */
    protected abstract boolean connect(Object serverLink);

    /**
     * Sends GameAction request to the Server
     * @param action GameAction requested by user
     * @return Response from the server indicating successful or failed request
     */
    protected abstract Response sendGameAction(GameAction action);

    public void sendState(GameState state) {
        for (ServerMessageListener listener : listeners)
            listener.onGameStateChange(state);
    }
}
