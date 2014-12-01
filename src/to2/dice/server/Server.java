package to2.dice.server;

import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;

import java.util.*;

/**
 * Created by Janusz on 30-11-2014.
 */

public class Server implements GameServer {
    Hashtable<Player, GameController> players = new Hashtable<Player, GameController>();
    Set<GameController> controllers = new HashSet<GameController>();

    public Server() {

    }

    /**
     *
     * @param login
     * @return
     */
    public Response login (String login) {
        Player player = new Player(login, false, 5);        //???
        if (players.contains(player)) {
            return new Response(Response.Type.FAILURE);
        } else {
            players.put(player, null);
            return new Response(Response.Type.SUCCESS);
        }
    }

    /**
     *
     * @param roomName
     * @param roomSettings
     * @param login
     * @return
     */
    public Response createRoom(String roomName, GameSettings roomSettings, String login) {
        GameControllerFactory gameControllerFactory = new GameControllerFactory();
        GameController gameController = gameControllerFactory.createGameControler(this, roomSettings, login);
        for (GameController c : controllers)
            if (c.getGameInfo().getSettings().getName().equals(gameController.getGameInfo().getSettings().getName())) return new Response(Response.Type.FAILURE);
        controllers.add(gameController);
        return new Response(Response.Type.SUCCESS);
    }

    /**
     *
     * @param action
     * @return
     */
    public Response handleGameAction (GameAction action) {
        Player player = getPlayer(action.getSender(), false);
        if (player == null) return new Response(Response.Type.FAILURE, "Not logged in");
        GameController gameController = players.get(player);
        Response response = gameController.handleGameAction(action);
        return response;
    }

    /**
     *
     * @return
     */
    public List<GameInfo> getRoomList() {
        List<GameInfo> roomList = new ArrayList<GameInfo>();
        for (GameController c : controllers) {
            roomList.add(c.getGameInfo());
        }
        return roomList;
    }

    /**
     *
     * @param login
     * @param create
     * @return
     */
    private Player getPlayer(String login, boolean create) {
        for (Player c : players.keySet())
            if (c.getName() == login) return c;
        if (create) {
            Player player = new Player(login, false, 5);    //???
            return player;
        } else {
            return null;
        }
    }

    /**
     *
     * @param gameController
     * @param gameState
     */
    @Override
    public void sendToAll(GameController gameController, GameState gameState) {

    }

    /**
     * 
     * @param gameController
     */
    @Override
    public void finishGame(GameController gameController) {

    }
}