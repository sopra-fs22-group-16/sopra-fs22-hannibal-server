package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

public interface ILobbyManager extends Iterable<ILobby>{

    ILobby removeLobbyWithId(long id);

    ILobby getLobbyWithId(long id);

    ILobby getLobbyWithName(String name);

    ILobby createLobby(String name, LobbyMode lobbyMode, IUser host) throws SmallestLobbyIdNotCreatable;

    void clear();
}
