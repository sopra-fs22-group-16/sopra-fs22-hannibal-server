package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;

import java.util.Collection;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility) throws SmallestIdNotCreatableException;

    Collection<ILobby> getLobbies();

    void clear();
}
