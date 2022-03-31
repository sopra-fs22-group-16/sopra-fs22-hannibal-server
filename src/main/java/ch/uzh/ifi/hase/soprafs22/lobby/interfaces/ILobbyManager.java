package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import java.util.List;

public interface ILobbyManager {
    void addLobby(ILobby lobby);

    ILobby removeLobby(int index);

    List<ILobby> getLobbyList();
}
