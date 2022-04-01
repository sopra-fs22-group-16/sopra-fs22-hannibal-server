package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, LobbyMode lobbyMode) throws SmallestIdNotCreatable;

    void clear();
}
