package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;

public interface ILobby extends Iterable<Player>{
    byte[] generateQrCode(String code);

    void changeReadyStatus(int token);

    LobbyMode getLobbyMode();

    void setLobbyMode(LobbyMode lobbyMode);

    void addPlayer(Player player);

    Player removePlayer(int index);

    void setGameMode(GameMode gameMode);

    void setGameType(GameType gameType);

    void startGame();

    long getId();

    String getName();

    Player getHost();

    GameMode getGameMode();

    GameType getGameType();

    String getInvitationCode();
}
