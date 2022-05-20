package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;

import java.util.List;

/**
 * Delta to be sent through web socket about the lobby so client can process info easier.
 */
public class LobbyDeltaWebSocketDTO {
    private boolean pullUpdate;
    private boolean redirectToGame;
    private List<Long> removedPlayerIdList;
    private Long nameChangedOfPlayerWithId;

    public boolean isPullUpdate() {
        return pullUpdate;
    }

    public void setPullUpdate(boolean pullUpdate) {
        this.pullUpdate = pullUpdate;
    }

    public boolean isRedirectToGame() {
        return redirectToGame;
    }

    public void setRedirectToGame(boolean redirectToGame) {
        this.redirectToGame = redirectToGame;
    }

    public List<Long> getRemovedPlayerIdList() {
        return removedPlayerIdList;
    }

    public void setRemovedPlayerIdList(List<Long> removedPlayerIdList) {
        this.removedPlayerIdList = removedPlayerIdList;
    }

    public Long getNameChangedOfPlayerWithId() {
        return nameChangedOfPlayerWithId;
    }

    public void setNameChangedOfPlayerWithId(Long nameChangedOfPlayerWithId) {
        this.nameChangedOfPlayerWithId = nameChangedOfPlayerWithId;
    }
}

