package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void createLobby_hostSet(){
        // Create new lobby
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        // Check
        Player host = lobby.getHost();
        assertNotNull(host);
        assertEquals(host, lobby.iterator().next());
    }

    @Test
    void createLobby_hostInformationSet(){
        // Create new lobby
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        // Check
        Player host = lobby.getHost();
        assertNotNull(host);
        assertNotNull(host.getToken());
        assertNotNull(host.getName());
        assertNotNull(host.getTeam());
        assertFalse(host.isReady());
    }
    
    @Test
    public void updateLobbyPlayer_userName_OK() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        Player host = lobby.getHost();

        lobby.setUserName(host.getToken(), "new username");

        assertEquals("new username", host.getName());
    }

    @Test
    public void updateLobbyPlayer_userNameDouble_OK() {
        // Test that old names are available for usage.
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        Player host = lobby.getHost();
        String oldUsername = host.getName();

        lobby.setUserName(host.getToken(), "new username");
        lobby.setUserName(host.getToken(), oldUsername);

        assertEquals(oldUsername, host.getName());
    }

    @Test
    public void updateLobbyPlayer_ready_OK() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        Player host = lobby.getHost();

        lobby.setReady(host.getToken(), true);

        assertTrue(host.isReady());
    }

    @Test
    public void updateLobbyPlayer_ready_notFound() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        assertHttpError(HttpStatus.NOT_FOUND, () -> lobby.setReady("invalid token", true));
    }

    @Test
    public void updateLobbyPlayer_userName_notFound() {
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        assertHttpError(HttpStatus.NOT_FOUND, () -> lobby.setUserName("invalid token", "new username"));
    }

    @Test
    public void updateLobbyPlayer_userNameNotUnique_conflict() {
        Lobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        Player host = lobby.getHost();
        Player newPlayer = lobby.generatePlayer();

        assertHttpError(HttpStatus.CONFLICT, () -> lobby.setUserName(host.getToken(), newPlayer.getName()));
    }

    private static void assertHttpError(HttpStatus expectedStatus, org.junit.jupiter.api.function.Executable executable) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, executable);
        assertEquals(expectedStatus, exception.getStatus());
    }

}