package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreateable;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

import java.util.List;

public interface ILobbyManager {
    void addLobby(ILobby lobby);

    ILobby removeLobby(int index);

    List<ILobby> getLobbyList();

    ILobby createLobby(IUser host) throws SmallestLobbyIdNotCreateable;
}
