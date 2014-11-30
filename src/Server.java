import java.util.HashSet;
import java.util.Hashtable;
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

    public boolean createRoom(String pokerid, String game, String message) {
        GameController newGameController = new GameController(pokerid, game, message);
        controllers.add(newGameController);
        newGameController.run();
        return true;
    }

    public boolean addPlayerToPoker(String pokerid, Player player) {
        for (GameController p : controllers)
            if (p.getPokerID() == pokerid) {
                if (players.put(player, p)==null) p.addPlayer(player);
                return true;
            }
        return false;
    }

    public void displayPlayersTable(String pokername){
        for (GameController p : controllers)
            if (p.getPokerID() == pokername)
                p.displayListOfplayers();
    }
}