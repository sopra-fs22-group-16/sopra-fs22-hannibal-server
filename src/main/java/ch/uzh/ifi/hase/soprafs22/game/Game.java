package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.interfaces.IMap;

public class Game {
    private final GameMode gameMode;
    private final GameType gameType;

    public Game(GameMode gameMode, GameType gameType){
        this.gameMode = gameMode;
        this.gameType = gameType;
    }

}
