package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(Long id);

    ILobby getLobbyWithId(Long id);

    ILobby createLobby(IUser host) throws SmallestLobbyIdNotCreatable;

    void clear();
}
