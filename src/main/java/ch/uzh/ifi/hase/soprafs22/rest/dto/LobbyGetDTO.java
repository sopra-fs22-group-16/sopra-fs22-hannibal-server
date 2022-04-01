package ch.uzh.ifi.hase.soprafs22.rest.dto;

// TODO: Add QR_Code

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;

import java.util.LinkedList;
import java.util.List;

public class LobbyGetDTO {

    Long lobbyId;

    String name;

    Long owner;

    List<PlayerGetDTO> members;

    LobbyMode visibility;

    GameMode gameMode;

    GameType ranked;

    String invitationCode;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
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

    public List<PlayerGetDTO> getMembers() {
        return members;
    }

    public void setMembers(LinkedList<PlayerGetDTO> members) {
        this.members = members;
    }

    public LobbyMode getVisibility() {
        return visibility;
    }

    public void setVisibility(LobbyMode visibility) {
        this.visibility = visibility;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameType getRanked() {
        return ranked;
    }

    public void setRanked(GameType ranked) {
        this.ranked = ranked;
    }
}
