package ch.uzh.ifi.hase.soprafs22.game.enums;

public enum GameMode {
    ONE_VS_ONE(2),
    TWO_VS_TWO(4);

    private final int maxNumbersOfPlayers;

    GameMode(int maxNumberOfPlayers){
        this.maxNumbersOfPlayers = maxNumberOfPlayers;
    }

    public int getMaxNumbersOfPlayers(){
        return maxNumbersOfPlayers;
    }

}
