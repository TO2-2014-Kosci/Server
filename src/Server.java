import java.util.HashSet;
import java.util.Set;

/**
 * Created by Janusz on 10-11-2014.
 */
public class Server {
    Set<Player> Players = new HashSet<Player>();
    Set<Poker> Pokers = new HashSet<Poker>();

    public Server() {
    }

    public void run() {
    }

    public boolean newPoker(String pokerid, String game, String message) {
        Poker newPoker = new Poker(pokerid, game, message);
        Pokers.add(newPoker);
        newPoker.run();
        return true;
    }

    public boolean addPlayerToPoker(String pokerid, Player player) {
        for (Poker p : Pokers)
            if (p.getPokerID() == pokerid) {
                if (Players.add(player)) p.addPlayer(player);
                return true;    //tak, ten return to swiadomie w tym miejscu
            }
        return false;
    }

    public void displayPlayersTable(String pokername){
        for (Poker p : Pokers)
            if (p.getPokerID() == pokername)
                p.displayListOfplayers();
    }
}