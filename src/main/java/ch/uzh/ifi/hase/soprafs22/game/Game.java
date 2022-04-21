package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.AmateurMapFactory;
import ch.uzh.ifi.hase.soprafs22.game.maps.BeginnerMapFactory;
import ch.uzh.ifi.hase.soprafs22.game.maps.interfaces.IMap;

public class Game {
    private GameMode gameMode;
    private GameType gameType;
    private IMap map;

    public Game(GameMode gameMode, GameType gameType) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        if(gameType==GameType.UNRANKED){
            map = new BeginnerMapFactory().createMap();
        }
        if(gameType==GameType.RANKED){
            map = new AmateurMapFactory().createMap();
        }
        this.map = map;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
