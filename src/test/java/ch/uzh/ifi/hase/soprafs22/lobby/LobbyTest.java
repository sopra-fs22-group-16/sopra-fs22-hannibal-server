package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void createLobby_hostSet(){
        // Create new lobby
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        // Check
        IPlayer host = lobby.getHost();
        assertNotNull(host);
        assertEquals(host, lobby.iterator().next());
    }

    @Test
    void createLobby_hostInformationSet(){
        // Create new lobby
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        // Check
        IPlayer host = lobby.getHost();
        assertNotNull(host);
        assertNotNull(host.getToken());
        assertNotNull(host.getName());
        assertNotNull(host.getTeam());
        assertFalse(host.isReady());
    }
    
    @Test
    public void updateLobbyPlayer_userName_OK() throws Exception {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        IPlayer host = lobby.getHost();

        lobby.setUserName(host.getToken(), "new username");

        assertEquals("new username", host.getName());
    }

    @Test
    public void updateLobbyPlayer_userNameDouble_OK() throws Exception  {
        // Test that old names are available for usage.
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        IPlayer host = lobby.getHost();
        String oldUsername = host.getName();

        lobby.setUserName(host.getToken(), "new username");
        lobby.setUserName(host.getToken(), oldUsername);

        assertEquals(oldUsername, host.getName());
    }

    @Test
    public void updateLobbyPlayer_ready_OK() throws Exception  {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        IPlayer host = lobby.getHost();

        lobby.setReady(host.getToken(), true);

        assertTrue(host.isReady());
    }

    @Test
    public void updateLobbyPlayer_ready_notFound() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        assertThrows(PlayerNotFoundException.class, () -> lobby.setReady("invalid token", true));
    }

    @Test
    public void updateLobbyPlayer_userName_notFound() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        assertThrows(PlayerNotFoundException.class, () -> lobby.setUserName("invalid token", "new username"));
    }

    @Test
    public void updateLobbyPlayer_userNameNotUnique_conflict() {
        Lobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        IPlayer host = lobby.getHost();
        IPlayer newPlayer = lobby.generatePlayer();
        lobby.addPlayer(newPlayer);

        assertThrows(DuplicateUserNameInLobbyException.class,  () -> lobby.setUserName(host.getToken(), newPlayer.getName()));
    }
}