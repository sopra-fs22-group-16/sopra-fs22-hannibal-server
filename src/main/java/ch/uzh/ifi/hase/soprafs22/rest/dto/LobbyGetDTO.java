package ch.uzh.ifi.hase.soprafs22.rest.dto;

// TODO: Add QR_Code or create own endpoint

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;

import java.util.LinkedList;
import java.util.List;

public class LobbyGetDTO {

    Long id;

    String name;

    Long owner;

    List<PlayerGetDTO> players;

    Visibility visibility;

    GameMode gameMode;

    GameType gameType;

    String invitationCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public List<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<PlayerGetDTO> players) {
        this.players = players;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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
