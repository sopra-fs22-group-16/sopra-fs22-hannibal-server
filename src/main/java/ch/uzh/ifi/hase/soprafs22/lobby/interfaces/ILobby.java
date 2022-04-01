package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

public interface ILobby extends Iterable<IUser>{
    byte[] generateQrCode(String code);

    void changeReadyStatus(int token);

    LobbyMode getLobbyMode();

    void setLobbyMode(LobbyMode lobbyMode);

    void addUser(IUser user);

    IUser removeUser(int index);

    void setGameMode(GameMode gameMode);

    void setGameType(GameType gameType);

    void startGame();

    long getId();

    String getName();

    IUser getHost();

    /**
     * Checks if the specified user is ready
     * @param user the specified user
     * @return true if the user is ready, false if the user is not ready or not in the lobby
     */
    boolean isUserReady(IUser user);

    GameMode getGameMode();

    GameType getGameType();

    String getInvitationCode();
}
