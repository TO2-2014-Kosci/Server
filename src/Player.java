/**
 * Created by Janusz on 11-11-2014.
 */
public class Player {
    private String login; //public? raczej nie
    private int age;
    public Poker table;

    public Player(String login, int age) {
        this.login = login;
        this.age = age;
    }

    public String getLogin() {
        return this.login;
    }
}
