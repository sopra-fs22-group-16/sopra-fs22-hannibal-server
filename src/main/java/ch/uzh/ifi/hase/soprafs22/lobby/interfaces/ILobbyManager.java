package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

import java.util.Collection;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility, RegisteredUser registeredUserAsHost) throws SmallestIdNotCreatableException;

    Collection<ILobby> getLobbiesCollection();

    void clear();

    void modifyPlayer(String token, Long lobbyId, String newName, Boolean ready) throws EmptyUsernameException, PlayerNotFoundException, DuplicateUserNameInLobbyException, RegisteredUserLobbyNameChangeException;

}
