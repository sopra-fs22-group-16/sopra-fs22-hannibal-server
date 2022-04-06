package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyManagerUpdateTest {

    private static LobbyManager lobbyManager;

    private ILobby LOBBY1;
    //private static final ILobby LOBBY2 = new Lobby("2L", LobbyMode.PUBLIC, USER2);

    @BeforeAll
    static void setup(){
        lobbyManager = LobbyManager.getInstance();
    }


    @BeforeEach
    public void before() throws SmallestIdNotCreatableException {
        //new Lobby("Lobby 1", LobbyMode.PRIVATE, /*host=*/ USER1);
        lobbyManager.clear();
        LOBBY1 = lobbyManager.createLobby("Lobby 1", Visibility.PUBLIC);
        //lobbyManager.addLobby(LOBBY2);
    }

    @Test
    void updateLobby_full1() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("RANKED");
        input.setVisibility("PRIVATE");

        lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input);

        ILobby result = lobbyManager.getLobbyWithId(0);
        assertEquals(input.getName(), result.getName());
        assertEquals(LOBBY1.getGameMode(), result.getGameMode());
        assertEquals(LOBBY1.getGameType(), result.getGameType());
        assertEquals(LOBBY1.getVisibility(), result.getVisibility());
    }

    @Test
    void updateLobby_full2() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("ONE_VS_ONE");
        input.setGameType("UNRANKED");
        input.setVisibility("PUBLIC");

        lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input);

        ILobby result = lobbyManager.getLobbyWithId(0);
        assertEquals(input.getName(), result.getName());
        assertEquals(GameMode.valueOf(input.getGameMode()), result.getGameMode());
        assertEquals(LOBBY1.getGameType(), result.getGameType());
        assertEquals(LOBBY1.getVisibility(), result.getVisibility());
    }

    @Test
    void updateLobby_noHost() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("ONE_VS_ONE");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyManager.updateLobby(LOBBY1.getOwner().getId()+1L, 0, input));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("401 UNAUTHORIZED \"User is not the host of the lobby.\"", exception.getMessage());
    }

    @Test
    void updateLobby_wrongVisibility() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("UNRANKED");
        input.setVisibility("badvisibility");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Visibility cannot be:badvisibility\"", exception.getMessage());
    }

    @Test
    void updateLobby_wrongGameType() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("wrongRANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"GameType cannot be:wrongRANKED\"", exception.getMessage());
    }

    @Test
    void updateLobby_wrongGameMode() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("new Name2");
        input.setGameMode("TWO_VS_ONE");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Mode cannot be:TWO_VS_ONE\"", exception.getMessage());
    }

    @Test
    void updateLobby_emptyName() {
        LobbyPutDTO input = new LobbyPutDTO();
        input.setName("   ");
        input.setGameMode("TWO_VS_TWO");
        input.setGameType("RANKED");
        input.setVisibility("PUBLIC");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyManager.updateLobby(LOBBY1.getOwner().getId(), 0, input));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("400 BAD_REQUEST \"Lobby name should not be empty.\"", exception.getMessage());
    }

}
