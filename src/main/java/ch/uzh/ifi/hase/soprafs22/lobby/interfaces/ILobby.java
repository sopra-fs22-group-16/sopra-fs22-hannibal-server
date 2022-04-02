package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

import java.util.UUID;

public interface ILobby {
    byte[] generateQrCode(String code);

    void changeReadyStatus(int token);

    LobbyMode getLobbyMode();

    void setLobbyMode(LobbyMode lobbyMode);

    void addUser(IUser user);

    IUser removeUser(long userId);

    void setGameMode(GameMode gameMode);

    void setGameType(GameType gameType);

    void startGame();

    String getName();

    void setName(String name);

    GameType getRanked();

    GameMode getMode();

    IUser getHost();

    long getId();
}
