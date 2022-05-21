package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

public class RegisteredUserGetDTO {

    private long id;

    private String username;

    private int rankedScore;

    private int wins;

    private int losses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
