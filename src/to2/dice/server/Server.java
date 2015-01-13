package to2.dice.server;

import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.messaging.*;

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
     * Remove existing player from players
     * @param login name of removed player
     * @return appropriate type of Response
     */
    public Response logout(String login) {
        if (login == null)
            return new Response(Response.Type.FAILURE, "Login couldn't be blank");
        if (players.containsKey(login)) {
            if (players.get(login)!=null) {
                GameAction leaveRoomAction = new GameAction(GameActionType.LEAVE_ROOM, login);
                players.get(login).handleGameAction(leaveRoomAction);
            }
            players.remove(login);
            return new Response(Response.Type.SUCCESS);
        } else {
            return new Response(Response.Type.FAILURE, "Player wasn't signed in");
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

        if (gameController == null)
            return new Response(Response.Type.FAILURE, "Failed to create game room");

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
        GameController gameController = null;

        if (action.getType() == GameActionType.JOIN_ROOM) {
            JoinRoomAction jra = (JoinRoomAction)action;
            for (GameController gc : controllers) {
                if (gc.getGameInfo().getSettings().getName().equals(jra.getGameRoom())) {
                    gameController = gc;
                    players.put(jra.getSender(), gc);
                    break;
                }
            }
        }
        else
            gameController = players.get(action.getSender());

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

    /**
     * Register Local Proxy
     * @param lcp new LCP
     */
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
        List<String> players = playersInRoom(gameController);

        for (LocalConnectionProxy lcp : localProxies) {
            if (players.contains(lcp.getLoggedInUser()))
                lcp.sendState(gameState);
        }

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
        for (String player : players.keySet())
            if (players.get(player) != null && players.get(player).equals(gameController))
                players.put(player, null);
    }

    private List<String> playersInRoom(GameController room) {
        if (this.controllers.size() == 0) return new ArrayList<String>();

        int initial = this.players.size() / this.controllers.size();
        ArrayList<String> result = new ArrayList<String>(initial);
        GameController curr = null;

        for (String player : players.keySet()) {
            curr = players.get(player);
            if (curr != null && curr.equals(room))
                result.add(player);
        }

        return result;
    }
}