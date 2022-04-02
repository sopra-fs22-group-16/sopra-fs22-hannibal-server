package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    public void createLobby_hostSet(){
        // Create new lobby
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);

        // Check
        Player host = lobby.getHost();
        assertNotNull(host);
        assertEquals(host, lobby.iterator().next());
    }

    @Test
    public void createLobby_hostInformationSet(){
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

}