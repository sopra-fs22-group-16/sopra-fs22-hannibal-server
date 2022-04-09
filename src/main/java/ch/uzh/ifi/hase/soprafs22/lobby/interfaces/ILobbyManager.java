package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, Visibility visibility) throws SmallestIdNotCreatableException;

    void clear();

    void modifyPlayer(String token, Long lobbyId, PlayerPutDTO playerPutDTO);
}
