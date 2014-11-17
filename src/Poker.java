import java.util.HashSet;
import java.util.Set;

/**
 * Created by Janusz on 11-11-2014.
 */
public class Poker {
    Set<Player> Players = new HashSet<Player>();
    private String PokerID, game, message;

    public Poker (String pokerid, String game, String message) {
        this.PokerID = pokerid;
        this.game = game;
        this.message = message;
    }

    public boolean run () {
        return true;
    }

    public String getPokerID() {
        return PokerID;
    }

    public boolean addPlayer (Player player) {
        return Players.add(player);
    }

    public void displayListOfplayers() {
        for (Player p : Players) {
            System.out.println(p.getLogin());
        }
    }
}