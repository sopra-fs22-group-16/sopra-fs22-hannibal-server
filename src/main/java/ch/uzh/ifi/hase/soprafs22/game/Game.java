package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;
import ch.uzh.ifi.hase.soprafs22.game.maps.MapLoader;

public class Game {
    private GameMode gameMode;
    private GameType gameType;
    private GameMap gameMap;

    public Game(GameMode gameMode, GameType gameType) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        //TODO Potential Feature: RANKED games get a harder map
        if(gameType==GameType.UNRANKED||gameType==GameType.RANKED){
            this.gameMap = new MapLoader().deserialize("beginner_map.json");
        }
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
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
