/**
 * Created by Janusz on 11-11-2014.
 */
public class Player {
    private String login; //public? raczej nie
    private int age;
    public GameController table;

    public Player(String login, int age) {
        this.login = login;
        this.age = age;
    }

    public String getLogin() {
        return this.login;
    }

   @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Player other = (Player) obj;
        if (login == null) {
            if (other.getLogin() != null) return false;
        } else if (!login.equals(other.getLogin())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        return result;
    }
}