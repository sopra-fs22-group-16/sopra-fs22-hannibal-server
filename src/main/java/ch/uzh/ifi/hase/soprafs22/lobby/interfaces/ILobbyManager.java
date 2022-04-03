package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    void updateLobby(long userId, long lobbyId, LobbyPutDTO lobbyPutDTO);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility) throws SmallestIdNotCreatable;

    boolean isLobbyNameInUse(String lobbyName);

    void clear();
}
