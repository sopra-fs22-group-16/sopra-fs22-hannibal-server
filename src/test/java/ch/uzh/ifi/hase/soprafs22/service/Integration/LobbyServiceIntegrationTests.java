package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class LobbyServiceIntegrationTests {

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        // Clear lobbyManager
        LobbyManager.getInstance().clear();
    }

    @Test
    void createLobby_unregisteredUser_validInputs_success(){
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // testUser
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, lobbyMode, gameMode, gameType);

        assertEquals(0L, createdLobby.getId());
        assertEquals(lobbyName, createdLobby.getName());
        assertEquals(gameMode, gameMode);
        assertEquals(gameType, gameType);
        assertNotNull(createdLobby.getHost());
        assertEquals(createdLobby.getHost(), createdLobby.iterator().next());
        assertNotNull(createdLobby.getHost().getTeam());
    }

    public static Stream<Arguments> provideDataForCreateLobbyNullAndEmptyParameters() {
        return Stream.of(
                Arguments.of(null, LobbyMode.PRIVATE, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("", LobbyMode.PRIVATE, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("lobbyName", null, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("lobbyName", LobbyMode.PRIVATE, null, GameType.UNRANKED),
                Arguments.of("lobbyName", LobbyMode.PRIVATE, GameMode.ONE_VS_ONE, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForCreateLobbyNullAndEmptyParameters")
    void createLobby_unregisteredUser_NullAndEmptyParameters_throwsException(String lobbyName, LobbyMode lobbyMode, GameMode gameMode, GameType gameType) {

        // attempt to create a lobby with missing information
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("", lobbyName, lobbyMode, gameMode, gameType));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createLobby_unregisteredUser_conflictLobbyName_throwsException() throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby with same name
        LobbyManager.getInstance().createLobby(lobbyName, LobbyMode.PRIVATE);

        // attempt to create second lobby with same name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("", lobbyName, lobbyMode, gameMode, gameType));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.CONFLICT.value());
    }

    @Test
    void createLobby_withToken_registeredUserNotFound_throwsException() throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby with same name
        LobbyManager.getInstance().createLobby(lobbyName, LobbyMode.PRIVATE);

        // attempt to create second lobby with same name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("token", lobbyName, lobbyMode, gameMode, gameType));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    void getLobby_validInputs_success() throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, lobbyMode);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to get lobby with id
        ILobby lobby = lobbyService.getLobby(createdLobby.getHost().getToken(), id);

        // Check
        assertEquals(createdLobby.getId(), lobby.getId());
        assertEquals(createdLobby.getName(), lobby.getName());
        assertEquals(createdLobby.getLobbyMode(), lobby.getLobbyMode());
        assertEquals(createdLobby.getGameMode(), lobby.getGameMode());
        assertEquals(createdLobby.getGameType(), lobby.getGameType());
        assertEquals(createdLobby.getHost(), lobby.getHost());

    }

    @ParameterizedTest
    @NullAndEmptySource
    void getLobby_emptyToken_throwException(String token) throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, lobbyMode);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby(token, id));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    void getLobby_noLobbyWithId_throwException() throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, lobbyMode);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to get nonexistent lobby with id 1L
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby("token", 1L));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.NOT_FOUND.value());

    }

    @Test
    void getLobby_wrongToken_throwException() throws SmallestIdNotCreatable {
        // given
        String lobbyName = "lobbyName";
        LobbyMode lobbyMode = LobbyMode.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, lobbyMode);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to get nonexistent lobby with id 1L
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby("wrongToken", id));

        // Check https status code
        assertEquals(exception.getRawStatusCode(), HttpStatus.FORBIDDEN.value());

    }


}
