package ch.uzh.ifi.hase.soprafs22.rest.dto;

// TODO: Add list of members
// TODO: Add QR_Code
// TODO: Add visibility
// TODO: ADD GameMode

public class LobbyGetDTO {

    Long lobbyId;

    String name;

    String invitationCode;

    Long owner;

    Long chatId;

    boolean ranked;

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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isRanked() {
        return ranked;
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }
}
