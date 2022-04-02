package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LobbyManagerTest {


    private LobbyManager lobbyManager;

    @BeforeEach
    public void setup() {
        lobbyManager = new LobbyManager();
        lobbyManager.addLobby(LOBBY1);
        //lobbyManager.addLobby(LOBBY2);
    }

    private static final IUser USER1 = new IUser() {
        @Override
        public Long getId() {
            return 1L;
        }

        @Override
        public String getToken() {
            return "token1";
        }

        @Override
        public String getUsername() {
            return "userName1";
        }
    };

    private static final IUser USER2 = new IUser() {
        @Override
        public Long getId() {
            return 2L;
        }

        @Override
        public String getToken() {
            return "token2";
        }

        @Override
        public String getUsername() {
            return "userName2";
        }
    };

    private static final ILobby LOBBY1 = new Lobby("Lobby 1", LobbyMode.PRIVATE, /*host=*/ USER1);
    private static final ILobby LOBBY2 = new Lobby("Lobby 2", LobbyMode.PUBLIC, USER2);



    @Test
    public void updateLobby_full1() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("RANKED");
        input.setVisibility("PRIVATE");

        lobbyManager.updateLobby(USER1.getId(), 0, input);

        ILobby result = lobbyManager.getLobbyById(0);
        assertEquals("new Name", result.getName());
        assertEquals(GameMode.TWO_VS_TWO, result.getMode());
        assertEquals(GameType.RANKED, result.getRanked());
        assertEquals(LobbyMode.PRIVATE, result.getLobbyMode());
    }

    @Test
    public void updateLobby_full2() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("ONE_VS_ONE");
        input.setGameType("UNRANKED");
        input.setVisibility("PUBLIC");

        lobbyManager.updateLobby(USER1.getId(), 0, input);

        ILobby result = lobbyManager.getLobbyById(0);
        assertEquals("new Name2", result.getName());
        assertEquals(GameMode.ONE_VS_ONE, result.getMode());
        assertEquals(GameType.UNRANKED, result.getRanked());
        assertEquals(LobbyMode.PUBLIC, result.getLobbyMode());
    }

    @Test
    public void updateLobby_noHost() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("ONE_VS_ONE");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> lobbyManager.updateLobby(USER2.getId(), 0, input));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("401 UNAUTHORIZED \"User is not the host of the lobby.\"", exception.getMessage());
    }

    @Test
    public void updateLobby_wrongVisibility() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("UNRANKED");
        input.setVisibility("badvisibility");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> lobbyManager.updateLobby(USER1.getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Visibility cannot be:badvisibility\"", exception.getMessage());
    }

    @Test
    public void updateLobby_wrongGameType() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("wrongRANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> lobbyManager.updateLobby(USER1.getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"GameType cannot be:wrongRANKED\"", exception.getMessage());
    }

    @Test
    public void updateLobby_wrongGameMode() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_ONE");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> lobbyManager.updateLobby(USER1.getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Mode cannot be:TWO_VS_ONE\"", exception.getMessage());
    }

    @Test
    public void updateLobby_emptyName() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("   ");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> lobbyManager.updateLobby(USER1.getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Lobby name should not be empty.\"", exception.getMessage());
    }

}


