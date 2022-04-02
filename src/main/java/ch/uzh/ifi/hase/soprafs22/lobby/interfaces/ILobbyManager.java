package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;

import java.util.List;

public interface ILobbyManager {
    void addLobby(ILobby lobby);

    ILobby removeLobby(long index);

    List<ILobby> getLobbyList();

    void updateLobby(long userId, long lobbyId, LobbyPutDTO lobbyPutDTO);

    ILobby getLobbyById(long lobbyId);
}
