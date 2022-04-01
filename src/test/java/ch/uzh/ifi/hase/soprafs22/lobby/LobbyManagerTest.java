package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    @Test
    public void createLobby_emptyLobbyList() {
        LobbyManager lobbyManager = LobbyManager.getInstance();
        try {
            // Clear lobbyManager lobby list
            lobbyManager.clear();

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE, host);

            // Assert that
            assertEquals(0L, lobby.getId());
            assertEquals("LobbyName", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
            assertEquals(host, lobby.getHost());
        }
        catch (SmallestLobbyIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createLobby_idIncreases() {
        LobbyManager lobbyManager = LobbyManager.getInstance();
        try {
            // Clear lobbyManager lobby list
            lobbyManager.clear();

            // Fill lobby list
            lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE, new User());

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName2", LobbyMode.PRIVATE, host);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName2", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
            assertEquals(host, lobby.getHost());
        }
        catch (SmallestLobbyIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createLobby_notContinuousIds_inList() {
        LobbyManager lobbyManager = LobbyManager.getInstance();
        try {
            // Clear lobbyManager lobby list
            lobbyManager.clear();

            // Fill lobby list
            lobbyManager.createLobby("LobbyName0", LobbyMode.PRIVATE, new User()); // id = 0
            ILobby lobby1 = lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE, new User()); // id = 1
            lobbyManager.createLobby("LobbyName2", LobbyMode.PRIVATE, new User()); // id = 2

            // Remove lobby1 to generate non-continuous lobby list
            lobbyManager.removeLobbyWithId(lobby1.getId());

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = lobbyManager.createLobby("LobbyName1", LobbyMode.PRIVATE, host);

            // Assert that
            assertEquals(1L, lobby.getId());
            assertEquals("LobbyName1", lobby.getName());
            assertEquals(LobbyMode.PRIVATE, lobby.getLobbyMode());
            assertEquals(host, lobby.getHost());
        }
        catch (SmallestLobbyIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void removeLobbyWithId_removed(){
        LobbyManager lobbyManager = LobbyManager.getInstance();

        // Clear lobbyManager lobby list
        lobbyManager.clear();


        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE, new User()); // id = 0

            // Remove from lobby list
            lobbyManager.removeLobbyWithId(lobby.getId());

            // check
            assertFalse(LobbyManager.getInstance().iterator().hasNext());

        }
        catch (SmallestLobbyIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getLobbyWithId(){
        LobbyManager lobbyManager = LobbyManager.getInstance();

        // Clear lobbyManager lobby list
        lobbyManager.clear();

        try {
            // Fill lobby list
            ILobby lobby = lobbyManager.createLobby("LobbyName", LobbyMode.PRIVATE, new User()); // id = 0

            // Remove from lobby list
            ILobby result = lobbyManager.getLobbyWithId(lobby.getId());

            // check
            assertEquals(lobby, result);

        }
        catch (SmallestLobbyIdNotCreatable e) {
            e.printStackTrace();
            fail();
        }
    }
}