package ch.uzh.ifi.hase.soprafs22.user;

public class RegisteredUser extends UserDecorator {
    private IUser user;
    private String password;
    private int rankedScore;
    private int wins;
    private int losses;

    /**
     * A registered user acts as a decorated user. It contains the user's properties and adds password, wins, losses,
     * and ranked score.
     * @param user
     */
    public RegisteredUser(IUser user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRankedScore() {
        return rankedScore;
    }

    public void setRankedScore(int rankedScore) {
        this.rankedScore = rankedScore;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    @Override
    public Long getId() {
        return this.user.getId();
    }

    @Override
    public String getToken() {
        return this.user.getToken();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }
}
