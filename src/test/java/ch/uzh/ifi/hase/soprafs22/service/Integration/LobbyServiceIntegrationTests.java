package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class LobbyServiceIntegrationTests {

    @Autowired
    private LobbyService lobbyService;

    @Qualifier("registeredUserRepository")
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Clear lobbyManager
        LobbyManager.getInstance().clear();
        userRepository.deleteAll();
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

    @Test
    void createLobby_registeredUser_validInputs_success() throws PlayerNotFoundException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // testUser
        ILobby createdLobby = lobbyService.createLobby(registeredUser.getToken(), lobbyName, visibility, gameMode, gameType);

        assertEquals(0L, createdLobby.getId());
        assertEquals(lobbyName, createdLobby.getName());
        assertEquals(gameMode, createdLobby.getGameMode());
        assertEquals(gameType, createdLobby.getGameType());
        assertNotNull(createdLobby.getHost());
        assertEquals(createdLobby.getHost(), createdLobby.iterator().next());
        assertNotNull(createdLobby.getHost().getTeam());

        RegisteredUser savedUser = createdLobby.getPlayer(registeredUser.getToken()).getRegisteredUser();
        assertEquals(registeredUser.getUsername(), savedUser.getUsername());
        assertEquals(registeredUser.getPassword(), savedUser.getPassword());
        assertEquals(registeredUser.getRankedScore(), savedUser.getRankedScore());
        assertEquals(registeredUser.getWins(), savedUser.getWins());
        assertEquals(registeredUser.getLosses(), savedUser.getLosses());
    }

    @Test
    void createRankedLobby_registeredUser_validInputs_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.RANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // testUser
        ILobby createdLobby = lobbyService.createLobby(registeredUser.getToken(), lobbyName, visibility, gameMode, gameType);

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
        LobbyManager.getInstance().createLobby(lobbyName, Visibility.PRIVATE, null);

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
        LobbyManager.getInstance().createLobby(lobbyName, Visibility.PRIVATE, null);

        // attempt to create second lobby with same name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("token", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void createRankedLobby_withoutToken_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.RANKED;

        // create lobby with same name
        LobbyManager.getInstance().createLobby(lobbyName, Visibility.PRIVATE, null);

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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        Long id = createdLobby.getId();

        lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), "newLobbyName", Visibility.PUBLIC, GameMode.TWO_VS_TWO, GameType.UNRANKED);

        assertEquals(createdLobby.getId(), id);
        assertEquals(createdLobby.getName(), "newLobbyName");
        assertEquals(createdLobby.getVisibility(), Visibility.PUBLIC);
        assertEquals(createdLobby.getGameMode(), GameMode.TWO_VS_TWO);
        assertEquals(createdLobby.getGameType(), GameType.UNRANKED);
    }

    @Test
    void updateToRankedLobby_name_visibility_gameMode_gameType_success() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, registeredUser);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.updateLobby(createdLobby, "wrongToken", lobbyName, visibility, gameMode, gameType));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void updateLobby_toRankedLobby_withUnregisteredUsers_throwsException() throws SmallestIdNotCreatableException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), lobbyName, Visibility.PUBLIC, GameMode.TWO_VS_TWO, GameType.RANKED));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        long id = createdLobby.getId();


        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getQRCodeFromLobby("token", id + 1L));

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
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, null);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);
        long id = createdLobby.getId();


        // Attempt to update lobby with empty name
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getQRCodeFromLobby("tokenNotInLobby", id));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getLobby_removeHostPlayer_removeLobby_success() {
        // given
        String lobbyName = "lobbyName";
        String token = "";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby lobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);

        // remove host of the lobby (there are no more players)
        lobbyService.removePlayerFromLobby(lobby.getHost().getToken(), lobby.getId());

        // check that there are no player in the lobby
        assertEquals(0, lobby.getNumberOfPlayers());

        // the lobby has been removed from the map
        Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobby(token, lobby.getId()));
    }

    @Test
    void getLobby_removeHostPlayer_assignNewHost_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // create lobby
        ILobby lobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);

        //add new player
        IPlayer newPlayer = lobbyService.addPlayer(null, lobby.getId(), null).getNewPlayer();

        // remove host of the lobby
        lobbyService.removePlayerFromLobby(lobby.getHost().getToken(), lobby.getId());

        // the new player is now the host
        assertEquals(newPlayer.getId(), lobby.getHost().getId());
    }

    @Test
    void createGame_1v1_unregisteredUser_validInputs_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        IPlayer player2 = lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null).getNewPlayer();

        // set players ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(player2.getToken(), createdLobby.getId(), null, true);

        // try to create a game
        Game game = lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId());

        assertEquals(createdLobby.getGame(), game);
        assertEquals(createdLobby.getGameMode(), game.getGameMode());
        assertEquals(createdLobby.getGameType(), game.getGameType());
        assertNotNull(game.getGameMap());
        assertFalse(game.hasEnded());
        assertTrue(game.isPlayersTurn(createdLobby.getHost().getToken()));

    }

    @Test
    void createGame_1v1_unregisteredUser_not_ready_throwsException() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null);

        // set only host ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);

        // Attempt to create lobby with not ready player
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createGame_1v1_unregisteredUser_rankedGame_throwsException() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        IPlayer player2 = lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null).getNewPlayer();

        // set gameType to ranked
        createdLobby.setGameType(GameType.RANKED);

        // set members ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(player2.getToken(), createdLobby.getId(), null, true);

        // Attempt to create a game with unregistered members
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createGame_1v1_unregisteredUser_lobbyNotComplete_throwsException() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a not complete lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);

        // set host ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);

        // Attempt to create a game with player 2 not ready
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createGame_1v1_unregisteredUser_emptyToken_throwsException(String token) {

        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        IPlayer player2 = lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null).getNewPlayer();

        // set members ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(player2.getToken(), createdLobby.getId(), null, true);

        // Attempt to create a game with null or empty token
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(token, createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());

    }

    @Test
    void createGame_1v1_unregisteredUser_notHost_throwsException() {

        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        IPlayer player2 = lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null).getNewPlayer();

        // set members ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(player2.getToken(), createdLobby.getId(), null, true);

        // Attempt to create a game while not being the host
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(player2.getToken(), createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

    }

    @Test
    void createGame_1v1_unregisteredUser_lobbyNotFound_throwsException() {

        // Attempt to create a game when to lobby with lobbyId exists
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame("token", 0L));

        // Check https status code
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    void createGame_1v1_unregisteredUser_gameAlreadyRunning_throwsException() {

        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        IPlayer player2 = lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), null).getNewPlayer();

        // set members ready
        lobbyService.modifyPlayer(createdLobby.getHost().getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(player2.getToken(), createdLobby.getId(), null, true);

        // start a game
        lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId());

        // Attempt to create a game with a game already running
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createGame(createdLobby.getHost().getToken(), createdLobby.getId()));

        // Check https status code
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

    }

    @Test
    void createLobby_exceedCapacityWhenChangingMode() {

        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PUBLIC;
        GameMode gameMode = GameMode.TWO_VS_TWO;
        GameType gameType = GameType.UNRANKED;

        // set up a full lobby
        ILobby createdLobby = lobbyService.createLobby("", lobbyName, visibility, gameMode, gameType);
        lobbyService.addPlayer(null, createdLobby.getId(), null);
        lobbyService.addPlayer(null, createdLobby.getId(), null);
        lobbyService.addPlayer(null, createdLobby.getId(), null);

        // change game mode to 1v1
        lobbyService.updateLobby(createdLobby, createdLobby.getHost().getToken(), lobbyName, visibility, GameMode.ONE_VS_ONE, gameType);

        // check that the capacity has been exceeded
        assert createdLobby.getNumberOfPlayers() > createdLobby.getLobbyCapacity();

        // check lobby capacity
        lobbyService.checkPlayersInLobby(createdLobby);

        // check that the number of players has been reduced to the maximum capacity
        assertEquals(createdLobby.getNumberOfPlayers(), createdLobby.getLobbyCapacity());

    }

    @Test
    void joinRankedLobby_registeredUser_success() throws SmallestIdNotCreatableException, PlayerNotFoundException {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.RANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // create lobby
        ILobby createdLobby = LobbyManager.getInstance().createLobby(lobbyName, visibility, registeredUser);
        createdLobby.setGameMode(gameMode);
        createdLobby.setGameType(gameType);

        // Create a second registered user

        RegisteredUser registeredUser2 = new RegisteredUser();
        registeredUser2.setUsername("registeredUser2");
        registeredUser2.setPassword("password2");
        registeredUser2.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser2);

        lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), registeredUser2.getToken());

        assertEquals(2, createdLobby.getNumberOfPlayers());
        RegisteredUser savedUser = createdLobby.getPlayer(registeredUser2.getToken()).getRegisteredUser();
        assertEquals(registeredUser2.getUsername(), savedUser.getUsername());
        assertEquals(registeredUser2.getPassword(), savedUser.getPassword());
        assertEquals(registeredUser2.getRankedScore(), savedUser.getRankedScore());
        assertEquals(registeredUser2.getWins(), savedUser.getWins());
        assertEquals(registeredUser2.getLosses(), savedUser.getLosses());
    }
}
