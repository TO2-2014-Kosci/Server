/**
 * Created by Janusz on 11-11-2014.
 */
public class GujlebGuj {
    public GujlebGuj() {
    }
    public void run () {
        Server server = new Server();

        server.newPoker("pokoj_malgosi", "gra w pokera", "parametry gry");

        Player zbyszek = new Player("zbyszek", 1);
        Player halinka = new Player("halinka", 1);
        Player zosia = new Player("zosia", 1);
        Player zygmunt = new Player("zygmunt", 1);
        Player boty_nic_nie_robio = new Player("boty_nic_nie_robio", 1);

        server.addPlayerToPoker("pokoj_malgosi", zbyszek);
        server.addPlayerToPoker("pokoj_malgosi", halinka);
        server.addPlayerToPoker("pokoj_malgosi", zosia);
        server.addPlayerToPoker("pokoj_malgosi", zygmunt);
        server.addPlayerToPoker("pokoj_malgosi", boty_nic_nie_robio);

        //dodaje kolejnych graczy o tym samym loginie
        server.addPlayerToPoker("pokoj_malgosi", zbyszek);
        server.addPlayerToPoker("pokoj_malgosi", boty_nic_nie_robio);

        server.displayPlayersTable("pokoj_malgosi");


    }
}
