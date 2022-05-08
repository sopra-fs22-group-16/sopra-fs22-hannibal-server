package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;

import java.util.List;
import java.util.Map;

public class GameGetDTO {
    private GameMode gameMode;
    private GameType gameType;
    private int turnNumber;
    private long playerIdCurrentTurn;
    private Map<Long, PlayerGetDTO> players;
    private GameMap gameMap;
    private List<UnitGetDTO> units;

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public long getPlayerIdCurrentTurn() {
        return playerIdCurrentTurn;
    }

    public void setPlayerIdCurrentTurn(long playerIdCurrentTurn) {
        this.playerIdCurrentTurn = playerIdCurrentTurn;
    }

    public Map<Long, PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Map<Long, PlayerGetDTO> players) {
        this.players = players;
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

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public List<UnitGetDTO> getUnits() {
        return units;
    }

    public void setUnits(List<UnitGetDTO> units) {
        this.units = units;
    }
}
