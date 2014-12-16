package to2.dice.messaging;

import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.server.ConnectionProxy;
import to2.dice.server.Server;
import to2.dice.server.ServerMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Fan on 2014-12-02.
 */
public class LocalConnectionProxy implements ConnectionProxy {

    private Server server;
    private List<ServerMessageListener> listeners;
    private String loggedInUser;

    public LocalConnectionProxy(Object serverLink, ServerMessageListener listener) {
        listeners = new ArrayList<ServerMessageListener>();
        connect(serverLink);
    }

    @Override
    public Response login(String login) {
        this.loggedInUser = login;
        return this.server.login(login);
    }

    @Override
    public List<GameInfo> getRoomList() {
        return this.server.getRoomList();
    }

    @Override
    public Response createRoom(GameSettings settings) {
        return this.server.createRoom(settings, loggedInUser);
    }

    @Override
    public Response joinRoom(String roomName) {
        GameAction action = new GameAction(GameActionType.JOIN_ROOM, loggedInUser);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response leaveRoom() {
        GameAction action = new GameAction(GameActionType.LEAVE_ROOM, loggedInUser);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response sitDown() {
        GameAction action = new GameAction(GameActionType.SIT_DOWN, loggedInUser);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response standUp() {
        GameAction action = new GameAction(GameActionType.STAND_UP, loggedInUser);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response reroll(boolean[] dice) {
        RerollAction action = new RerollAction(loggedInUser, dice);
        return this.server.handleGameAction(action);
    }

    protected boolean connect(Object serverLink) {
        server = (Server) serverLink;
        server.registerLocalProxy(this);

        return this.server != null;
    }

    @Override
    public void addServerMessageListener(ServerMessageListener listener) {
        listeners.add(listener);
    }

    public void sendState(GameState s) {
        for (ServerMessageListener sml : listeners)
            sml.onGameStateChange(s);
    }
}
