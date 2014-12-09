package to2.dice.messaging;

import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.server.ConnectionProxy;
import to2.dice.server.Server;
import to2.dice.server.ServerMessageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2014-12-02.
 */
public class LocalConnectionProxy extends ConnectionProxy {

    private Server server;

    public LocalConnectionProxy(Object serverLink, ServerMessageListener listener) {
        super(serverLink, listener);
    }

    @Override
    public Response login(String login) {
        return this.server.login(login);
    }

    @Override
    public List<GameInfo> getRoomList() {
        return this.server.getRoomList();
    }

    @Override
    public Response createRoom(GameSettings settings, String login) {
        return this.server.createRoom(settings, login);
    }

    @Override
    public Response joinRoom(String roomName, String login) {
        GameAction action = new GameAction(GameActionType.JOIN_ROOM, login);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response leaveRoom(String login) {
        GameAction action = new GameAction(GameActionType.LEAVE_ROOM, login);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response sitDown(String login) {
        GameAction action = new GameAction(GameActionType.SIT_DOWN, login);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response standUp(String login) {
        GameAction action = new GameAction(GameActionType.STAND_UP, login);
        return this.server.handleGameAction(action);
    }

    @Override
    public Response reroll(boolean[] dice, String login) {
        RerollAction action = new RerollAction(login, dice);
        return this.server.handleGameAction(action);
    }

    @Override
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
