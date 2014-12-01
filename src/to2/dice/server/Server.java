package to2.dice.server;

import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;

import java.util.*;

/**
 * Created by Janusz on 30-11-2014.
 */

public class Server implements GameServer {
    HashMap<String, GameController> players = new HashMap<String, GameController>();
    Set<GameController> controllers = new HashSet<GameController>();

    public Server() {

    }

    /**
     * Create a new player with parameter login as a player's name
     * @param login name of created player
     * @return appropriate type of Response
     */
    public Response login (String login) {
        if (players.containsKey(login)) {
            return new Response(Response.Type.FAILURE);
        } else {
            players.put(login, null);
            return new Response(Response.Type.SUCCESS);
        }
    }

    /**
     * Create a new instance of GameController using GameControllerFactory
     * @param roomName name of created room
     * @param roomSettings settings of created room
     * @param creator name of the first player in the room
     * @return appropriate type of Response
     */
    public Response createRoom(String roomName, GameSettings roomSettings, String creator) {
        GameControllerFactory gameControllerFactory = new GameControllerFactory();
        GameController gameController = gameControllerFactory.createGameControler(this, roomSettings, creator);
        for (GameController c : controllers)
            if (c.getGameInfo().getSettings().getName().equals(gameController.getGameInfo().getSettings().getName())) return new Response(Response.Type.FAILURE);
        controllers.add(gameController);
        return new Response(Response.Type.SUCCESS);
    }

    /**
     * Perform action (instance of GameAction) of appropriate type
     * @param action parameters of performed action
     * @return appropriate type of Response
     */
    public Response handleGameAction (GameAction action) {
        GameController gameController = players.get(action.getSender());
        Response response = gameController.handleGameAction(action);
        return response;
    }

    /**
     * Get the GameInfo from controllers and return as a list.
     * @return new List of GameInfo
     */
    public List<GameInfo> getRoomList() {
        List<GameInfo> roomList = new ArrayList<GameInfo>();
        for (GameController c : controllers) {
            roomList.add(c.getGameInfo());
        }
        return roomList;
    }

    /**
     * Send gameState to all players who are in the selected room.
     * @param gameController instance of GameController where there are players to whom we send gameState
     * @param gameState message that is sending to players
     */
    @Override
    public void sendToAll(GameController gameController, GameState gameState) {

    }

    /**
     * Remove finished game.
     * @param gameController finished game
     */
    @Override
    public void finishGame(GameController gameController) {
        for (GameController c : controllers)
            if (c.getGameInfo().getSettings().getName().equals(gameController.getGameInfo().getSettings().getName())) {
                controllers.remove(c);
                return;
            }
    }
}