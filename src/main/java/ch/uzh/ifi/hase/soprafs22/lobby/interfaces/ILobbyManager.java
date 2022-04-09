package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    void updateLobby(long userId, long lobbyId, LobbyPutDTO lobbyPutDTO);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility) throws SmallestIdNotCreatableException;

    boolean isLobbyNameInUse(String lobbyName);

    void clear();

    void modifyPlayer(String token, Long lobbyId, PlayerPutDTO playerPutDTO);
}
