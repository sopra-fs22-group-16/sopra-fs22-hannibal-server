package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import org.springframework.web.client.RestClientException;

public interface ILobby extends Iterable<Player>{

    void generateQrCode() throws RestClientException;

    byte[] getQrCode();

    void changeReadyStatus(String token);

    Visibility getVisibility();

    void setVisibility(Visibility visibility);

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
