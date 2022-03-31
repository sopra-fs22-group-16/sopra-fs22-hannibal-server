package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreateable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagerTest {

    @Test
    public void createLobby_emptyLobbyList() {
        try {
            // Clear lobbyManager lobby list
            LobbyManager.getInstance().getLobbyList().clear();

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = LobbyManager.getInstance().createLobby(host);

            // Assert that
            assertEquals(lobby.getId(), 0L);
            assertEquals(lobby.getName(), "Lobby-0");
            assertEquals(lobby.getLobbyMode(), LobbyMode.PRIVATE);
            assertEquals(lobby.getHost(), host);
        }
        catch (SmallestLobbyIdNotCreateable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createLobby_continuousIds_inList() {
        try {
            // Clear lobbyManager lobby list
            LobbyManager.getInstance().getLobbyList().clear();

            // Fill lobby list
            LobbyManager.getInstance().addLobby(new Lobby(0L, "Lobby-0", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(1L, "Lobby-1", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(2L, "Lobby-2", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(3L, "Lobby-3", LobbyMode.PRIVATE, new User()));

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = LobbyManager.getInstance().createLobby(host);

            // Assert that
            assertEquals(lobby.getId(), 4L);
            assertEquals(lobby.getName(), "Lobby-4");
            assertEquals(lobby.getLobbyMode(), LobbyMode.PRIVATE);
            assertEquals(lobby.getHost(), host);
        }
        catch (SmallestLobbyIdNotCreateable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createLobby_notContinuousIds_inList() {
        try {
            // Clear lobbyManager lobby list
            LobbyManager.getInstance().getLobbyList().clear();

            // Fill lobby list
            LobbyManager.getInstance().addLobby(new Lobby(0L, "Lobby-0", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(1L, "Lobby-1", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(5L, "Lobby-2", LobbyMode.PRIVATE, new User()));
            LobbyManager.getInstance().addLobby(new Lobby(9L, "Lobby-3", LobbyMode.PRIVATE, new User()));

            // Create host
            IUser host = new User();

            // Create a new lobby
            ILobby lobby = LobbyManager.getInstance().createLobby(host);

            // Assert that
            assertEquals(lobby.getId(), 2L);
            assertEquals(lobby.getName(), "Lobby-2");
            assertEquals(lobby.getLobbyMode(), LobbyMode.PRIVATE);
            assertEquals(lobby.getHost(), host);
        }
        catch (SmallestLobbyIdNotCreateable e) {
            e.printStackTrace();
        }
    }
}