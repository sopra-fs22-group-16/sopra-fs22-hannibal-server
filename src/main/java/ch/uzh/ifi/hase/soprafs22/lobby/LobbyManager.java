package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;

import java.util.List;

public class LobbyManager implements ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager {
    private List<ILobby> lobbyList;

    @Override
    public void addLobby(ILobby lobby) {
        this.lobbyList.add(lobby);
    }

    @Override
    public ILobby removeLobby(int index) {
        return this.lobbyList.remove(index);
    }

    @Override
    public List<ILobby> getLobbyList() {
        return this.lobbyList;
    }
}
