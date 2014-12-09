package to2.dice.server;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.LocalConnectionProxy;
import to2.dice.messaging.Response;

import java.util.*;

/**
 * Created by Janusz on 30-11-2014.
 */

public class Server implements GameServer {
    HashMap<String, GameController> players;
    Set<GameController> controllers;
    Set<LocalConnectionProxy> localProxies;

    public Server() {
        players = new HashMap<String, GameController>();
        controllers = new HashSet<GameController>();
        localProxies = new HashSet<LocalConnectionProxy>();
    }

    /**
     * Create a new player with parameter login as a player's name
     * @param login name of created player
     * @return appropriate type of Response
     */
    public Response login(String login) {
        if (login == null || login.replaceAll(" ", "").length() == 0)
            return new Response(Response.Type.FAILURE, "Login can't be blank");

        if (players.containsKey(login)) {
            return new Response(Response.Type.FAILURE, "Login is not unique");
        } else {
            players.put(login, null);
            return new Response(Response.Type.SUCCESS);
        }
    }

    /**
     * Create a new instance of GameController using GameControllerFactory
     * @param roomSettings settings of created room
     * @param creator name of the first player in the room
     * @return appropriate type of Response
     */
    public Response createRoom(GameSettings roomSettings, String creator) {
        if (!players.keySet().contains(creator))
            return new Response(Response.Type.FAILURE, String.format("User %s not logged in", creator));

        String newName = roomSettings.getName();

        for (GameController c : controllers)
            if (c.getGameInfo().getSettings().getName().equals(newName))
                return new Response(Response.Type.FAILURE, String.format("Game room with name %s already exists", newName));

        GameController gameController = GameControllerFactory.createGameControler(this, roomSettings, creator);
        controllers.add(gameController);
        players.put(creator, gameController);

        return new Response(Response.Type.SUCCESS);
    }

    /**
     * Perform action (instance of GameAction) of appropriate type
     * @param action parameters of performed action
     * @return appropriate type of Response
     */
    public Response handleGameAction(GameAction action) {
        GameController gameController = players.get(action.getSender());

        if (gameController == null)
            return new Response(Response.Type.FAILURE, "User is not in any room");

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

    public void registerLocalProxy(LocalConnectionProxy lcp) {
        localProxies.add(lcp);
    }

    /**
     * Send gameState to all players who are in the selected room.
     * @param gameController instance of GameController where there are players to whom we send gameState
     * @param gameState message that is sending to players
     */
    @Override
    public void sendToAll(GameController gameController, GameState gameState) {
        for (LocalConnectionProxy lcp : localProxies)
            lcp.sendState(gameState);

        // TODO Remote
    }

    /**
     * Remove finished game.
     * @param gameController finished game
     */
    @Override
    public void finishGame(GameController gameController) {
        //TODO Sending information to players

        controllers.remove(gameController);
    }
}