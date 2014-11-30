import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by Janusz on 10-11-2014.
 */

public class Server {
    Hashtable<Player, GameController> players = new Hashtable<Player, GameController>();
    Set<GameController> controllers = new HashSet<GameController>();

    public Server() {
    }

    public void run() {
    }

    public Response login (String login) {
        Player player = new Player();
        player.setLogin(login);

        if (players.contains(player)) {
            return new Response(Response.Type.FAILURE);
        } else {
            players.put(player, null);
            return new Response(Response.Type.SUCCESS);
        }
    }

    public Response createRoom(String roomName, GameSettings roomSettings, String login) {
        GameController gameController = createGameControler(this, roomSettings); //???
        for (GameController c : controllers)
            if (c.roomName.equals(gameController.roomName)) return new Response(Response.Type.FAILURE);
        controllers.add(gameController);
        return new Response(Response.Type.SUCCESS);
    }

    public Response handleGameAction (GameActionType action) {
        Player player = getPlayer(action.getLogin(), false);
        if (player == null) return new Response(ResponseType.FAILURE, "Not logged in");
        GameController gameController = controllers.get(plyer);
        Response response = new Response(gameController.handleGameAction(action));
        return response;
    }

    public List<GameInfo> getRoomList() {
        List<GameInfo> roomList = new List<GameInfo>();
        for (GameController c : controllers) {
            GameInfo gameInfo = c.getGameInfo;
            roomList.add(gameInfo);
        }
        return roomList;
    }

    private Player getPlayer(String login, boolean create) {
        for (Player c : players)
            if (c.getLogin == login) return c;
        if (create) {
            Player player = new Player();
            player.setLogin(login);
            return player;
        } else {
            return null;
        }
    }
}