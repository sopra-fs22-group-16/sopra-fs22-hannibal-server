package ch.uzh.ifi.hase.soprafs22.lobby.interfaces;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.FullLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import org.springframework.web.client.RestClientException;

public interface ILobby extends Iterable<IPlayer>{

    byte[] getQrCode() throws RestClientException;

    void changeReadyStatus(String token);

    Visibility getVisibility();

    void setVisibility(Visibility visibility);

    IPlayer removePlayer(String token);

    void setGameMode(GameMode gameMode);

    void setGameType(GameType gameType);

    void startGame();

    Game getGame();

    long getId();

    String getName();

    void setName(String name);

    IPlayer getHost();

    GameMode getGameMode();

    GameType getGameType();

    String getInvitationCode();

    void setUserName(String token, String newName) throws DuplicateUserNameInLobbyException, PlayerNotFoundException;

    void setReady(String token, Boolean ready) throws PlayerNotFoundException;

    int getNumberOfPlayers();

    void assignNewHost();

    IPlayer getPlayer(String token) throws PlayerNotFoundException;

    IPlayer generatePlayer();

    void addPlayer(IPlayer player) throws FullLobbyException;

    int getLobbyCapacity();

    int reducePlayersInLobby();

    boolean setAllPlayersNotReady();

    boolean balanceTeams();
}
