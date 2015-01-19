package to2.dice.server;

import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.GameInfo;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.messaging.*;

import java.util.*;

/**
 * @author Janusz
 * @version 0.3
 */

public class Server implements GameServer {
    HashMap<String, GameController> players;
    Set<GameController> controllers;
    Set<LocalConnectionProxy> localProxies;

    MessageServer messageServer;

    public Server() {
        this("localhost");
    }

    public Server(String queueServer) {
        players = new HashMap<String, GameController>();
        controllers = new HashSet<GameController>();
        localProxies = new HashSet<LocalConnectionProxy>();

        messageServer = new MessageServer(this, queueServer);
        messageServer.run();
    }

    /**
     * Create a new player with parameter login as a player's name
     * @param login name of created player
     * @return appropriate type of Response
     */
    public Response login(String login) {
        if (login == null || login.replaceAll(" ", "").length() == 0)
            return new Response(Response.Type.FAILURE, "Login nie może być pusty");

        if (players.containsKey(login)) {
            return new Response(Response.Type.FAILURE, "Login nie jest unikalny");
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
            return new Response(Response.Type.FAILURE, "Nie można wylogować użytkownika bez loginu");
        if (players.containsKey(login)) {
            if (players.get(login)!=null) {
                GameAction leaveRoomAction = new GameAction(GameActionType.LEAVE_ROOM, login);
                players.get(login).handleGameAction(leaveRoomAction);
            }
            players.remove(login);
            return new Response(Response.Type.SUCCESS);
        } else {
            return new Response(Response.Type.FAILURE, "Użytkownik nie był zalogowany");
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
            return new Response(Response.Type.FAILURE, String.format("Użytkownik %s nie jest zalogowany", creator));

        String newName = roomSettings.getName();

        for (GameController c : controllers)
            if (c.getGameInfo().getSettings().getName().equals(newName))
                return new Response(Response.Type.FAILURE, String.format("Pokój o nazwie %s już istnieje", newName));

        GameController gameController = GameControllerFactory.createGameControler(this, roomSettings, creator);

        if (gameController == null)
            return new Response(Response.Type.FAILURE, "Nie udało się stworzyć pokoju");

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
            GameController controller;
            if ((controller = players.get(action.getSender())) != null)
                return new Response(Response.Type.FAILURE,
                        String.format("Jesteś już w pokoju %s", controller.getGameInfo().getSettings().getName()));

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
            return new Response(Response.Type.FAILURE, "Użytkownik nie jest w żadnym pokoju");

        Response response = gameController.handleGameAction(action);
        if (response.isSuccess() && action.getType() == GameActionType.LEAVE_ROOM)
            players.put(action.getSender(), null);
        if (!response.isSuccess() && action.getType() == GameActionType.JOIN_ROOM)
            players.put(action.getSender(), null);

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
        String roomName = gameController.getGameInfo().getSettings().getName();

        for (LocalConnectionProxy lcp : localProxies) {
            if (lcp.receivesGameStateFrom(roomName))
                lcp.sendState(gameState);
        }

        messageServer.pushState(gameController, gameState);
    }

    /**
     * Remove finished game.
     * @param gameController finished game
     */
    @Override
    public void finishGame(GameController gameController) {
        controllers.remove(gameController);
        for (String player : players.keySet())
            if (players.get(player) != null && players.get(player).equals(gameController))
                players.put(player, null);
    }

    public void close() {
       messageServer.close();
    }
}