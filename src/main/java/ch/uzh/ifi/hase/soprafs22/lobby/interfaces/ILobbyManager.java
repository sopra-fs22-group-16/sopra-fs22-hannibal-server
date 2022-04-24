package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.EmptyUsernameException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;

import java.util.Collection;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility) throws SmallestIdNotCreatableException;

    Collection<ILobby> getLobbiesCollection();

    void clear();

    void modifyPlayer(String token, Long lobbyId, String newName, Boolean ready) throws EmptyUsernameException, PlayerNotFoundException, DuplicateUserNameInLobbyException;

}
