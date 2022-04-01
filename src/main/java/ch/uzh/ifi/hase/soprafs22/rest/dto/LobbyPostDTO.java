package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;

public class LobbyPostDTO {

    private String name;

    private LobbyMode lobbyMode;

    private GameMode gameMode;

    private GameType gameType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LobbyMode getLobbyMode() {
        return lobbyMode;
    }

    public void setLobbyMode(LobbyMode lobbyMode) {
        this.lobbyMode = lobbyMode;
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
