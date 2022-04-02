package ch.uzh.ifi.hase.soprafs22.game.enums;

public enum Team {
    Red(0),
    Blue(1);

    // continuous number in [0,Team.values().length)
    private final int teamNumber;

    Team(int teamNumber){
        this.teamNumber = teamNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }
}
