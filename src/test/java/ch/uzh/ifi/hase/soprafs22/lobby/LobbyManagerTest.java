package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    private static LobbyManager lobbyManager;

    @BeforeAll
    static void setup(){
        lobbyManager = LobbyManager.getInstance();
    }

    @BeforeEach
    public void before(){
        // Clear lobbyManager lobby list
        lobbyManager.clear();
    }

    @Test
    public void createLobby_emptyLobbyList() {
        try {
            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE);

            // Assert that
            assertEquals(0L, lobby.getId());
            assertEquals("LobbyName", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
        }
        catch (SmallestIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createLobby_idIncreases() {
        try {
            // Fill lobby list
            lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE);

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName2", LobbyMode.PRIVATE);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName2", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
        }
        catch (SmallestIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createLobby_notContinuousIds_inList() {
        try {
            // Fill lobby list
            lobbyManager.createLobby("LobbyName0", LobbyMode.PRIVATE); // id = 0
            ILobby lobby1 = lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE); // id = 1
            lobbyManager.createLobby("LobbyName2", LobbyMode.PRIVATE); // id = 2

            // Remove lobby1 to generate non-continuous lobby list
            lobbyManager.removeLobbyWithId(lobby1.getId());

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName1", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
        }
        catch (SmallestIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void removeLobbyWithId_removed(){
        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE); // id = 0

            // Remove from lobby list
            lobbyManager.removeLobbyWithId(lobby.getId());

            // check
            assertFalse(LobbyManager.getInstance().iterator().hasNext());

        }
        catch (SmallestIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getLobbyWithId(){
        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE); // id = 0

            // Remove from lobby list
            ILobby result = lobbyManager.getLobbyWithId(lobby.getId());

            // check
            assertEquals(lobby, result);

        }
        catch (SmallestIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }
}