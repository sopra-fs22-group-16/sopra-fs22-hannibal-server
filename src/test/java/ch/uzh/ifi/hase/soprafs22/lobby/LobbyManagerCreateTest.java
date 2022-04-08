package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerCreateTest {

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
    void createLobby_emptyLobbyList() {
        try {
            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName", Visibility.PRIVATE);

            // Assert that
            assertEquals(0L, lobby.getId());
            assertEquals("LobbyName", lobby.getName());
            assertEquals(Visibility.PRIVATE, lobby.getVisibility());
        }
        catch (SmallestIdNotCreatableException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void createLobby_idIncreases() {
        try {
            // Fill lobby list
            lobbyManager.createLobby("LobbyName1", Visibility.PRIVATE);

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName2", Visibility.PRIVATE);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName2", lobby.getName());
            assertEquals(Visibility.PRIVATE, lobby.getVisibility());
        }
        catch (SmallestIdNotCreatableException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void createLobby_notContinuousIds_inList() {
        try {
            // Fill lobby list
            lobbyManager.createLobby("lobbyName0", Visibility.PRIVATE); // id = 0
            ILobby lobby1 = lobbyManager.createLobby("lobbyName1", Visibility.PRIVATE); // id = 1
            lobbyManager.createLobby("lobbyName2", Visibility.PRIVATE); // id = 2

            // Remove lobby1 to generate non-continuous lobby list
            lobbyManager.removeLobbyWithId(lobby1.getId());

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName1", Visibility.PRIVATE);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName1", lobby.getName());
            assertEquals(Visibility.PRIVATE, lobby.getVisibility());
        }
        catch (SmallestIdNotCreatableException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void removeLobbyWithId_removed(){
        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("lobbyName", Visibility.PRIVATE); // id = 0

            // Remove from lobby list
            lobbyManager.removeLobbyWithId(lobby.getId());

            // check
            assertFalse(LobbyManager.getInstance().iterator().hasNext());

        }
        catch (SmallestIdNotCreatableException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getLobbyWithId(){
        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("lobbyName", Visibility.PRIVATE); // id = 0

            // Remove from lobby list
            ILobby result = lobbyManager.getLobbyWithId(lobby.getId());

            // check
            assertEquals(lobby, result);

        }
        catch (SmallestIdNotCreatableException e) {
            e.printStackTrace();
            fail();
        }
    }
}