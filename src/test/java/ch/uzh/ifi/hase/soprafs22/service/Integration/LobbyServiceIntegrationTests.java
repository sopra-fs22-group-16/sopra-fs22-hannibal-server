package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class LobbyServiceIntegrationTests {

    @Autowired
    private LobbyService lobbyService;

    @BeforeEach
    public void setup() {
        // Clear lobbyManager
        LobbyManager.getInstance().clear();
    }

    @Test
    void createLobby_unregisteredUser_validInputs_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // testUser
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);

        assertEquals(0L, createdLobby.getId());
        assertEquals(lobbyName, createdLobby.getName());
        assertEquals(gameMode, createdLobby.getGameMode());
        assertEquals(gameType, createdLobby.getGameType());
        assertNotNull(createdLobby.getHost());
        assertEquals(createdLobby.getHost(), createdLobby.iterator().next());
        assertNotNull(createdLobby.getHost().getTeam());
    }

    public static Stream<Arguments> provideDataForCreateLobbyNullAndEmptyParameters() {
        return Stream.of(
                Arguments.of(null, Visibility.PRIVATE, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("", Visibility.PRIVATE, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("lobbyName", null, GameMode.ONE_VS_ONE, GameType.UNRANKED),
                Arguments.of("lobbyName", Visibility.PRIVATE, null, GameType.UNRANKED),
                Arguments.of("lobbyName", Visibility.PRIVATE, GameMode.ONE_VS_ONE, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForCreateLobbyNullAndEmptyParameters")
    void createLobby_unregisteredUser_NullAndEmptyParameters_throwsException(String lobbyName, Visibility visibility, GameMode gameMode, GameType gameType) {

        // attempt to create a lobby with missing information
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createLobby_unregisteredUser_conflictLobbyName_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby with same name
        LobbyManager.getInstance().createLobby(lobbyName, Visibility.PRIVATE);

        // attempt to create second lobby with same name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createLobby_withToken_registeredUserNotFound_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby with same name
        LobbyManager.getInstance().createLobby(lobbyName, Visibility.PRIVATE);

        // attempt to create second lobby with same name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("token", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getLobby_validInputs_success() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to get lobby with id
        ILobby lobby = lobbyService.getLobby(createdLobby.getHost().getToken(), id);

        // Check
        assertEquals(createdLobby.getId(), lobby.getId());
        assertEquals(createdLobby.getName(), lobby.getName());
        assertEquals(createdLobby.getVisibility(), lobby.getVisibility());
        assertEquals(createdLobby.getGameMode(), lobby.getGameMode());
        assertEquals(createdLobby.getGameType(), lobby.getGameType());
        assertEquals(createdLobby.getHost(), lobby.getHost());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getLobby_emptyToken_throwException(String token) throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby(token, id));

        // Check https status code
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void getLobby_noLobbyWithId_throwException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to get nonexistent lobby with id 1L
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby("token", 1L));

        // Check https status code
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getLobby_wrongToken_throwException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        // Attempt to get nonexistent lobby with id 1L
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby("wrongToken", id));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void updateLobby_name_visibility_gameMode_gameType_success() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), "newLobbyName", Visibility.PUBLIC, GameMode.TWO_VS_TWO, GameType.RANKED);

        assertEquals(createdLobby.getId(), id);
        assertEquals(createdLobby.getName(), "newLobbyName");
        assertEquals(createdLobby.getVisibility(), Visibility.PUBLIC);
        assertEquals(createdLobby.getGameMode(), GameMode.TWO_VS_TWO);
        assertEquals(createdLobby.getGameType(), GameType.RANKED);
    }

    @Test
    void updateLobby_emptyName_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), "", visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateLobby_nullConfig_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), lobbyName, null, null, null));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateLobby_wrongToken_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.updateLobby(createdLobby, "wrongToken", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getQRCode_nullOrEmptyToken_throwsException(String token) throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();


        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getQRCodeFromLobby(token, id));

        // Check https status code
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void getQRCode_lobbyNotFound_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        long id = createdLobby.getId();


        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getQRCodeFromLobby("token", id+1L));

        // Check https status code
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getQRCode_tokenNotInLobby_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        long id = createdLobby.getId();


        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getQRCodeFromLobby("tokenNotInLobby", id));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }


}
