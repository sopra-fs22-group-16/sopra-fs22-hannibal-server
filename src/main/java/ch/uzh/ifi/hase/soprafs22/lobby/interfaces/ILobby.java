package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;

public interface ILobby extends Iterable<Player>{
    byte[] generateQrCode(String code);

    void changeReadyStatus(String token);

    Visibility getLobbyMode();

    void setLobbyMode(Visibility visibility);

    Player addPlayer();

    Player removePlayer(String token);

    void setGameMode(GameMode gameMode);

    void setGameType(GameType gameType);

    void startGame();

    long getId();

    String getName();

    void setName(String name);

    Player getHost();

    GameMode getGameMode();

    GameType getGameType();

    String getInvitationCode();
}
