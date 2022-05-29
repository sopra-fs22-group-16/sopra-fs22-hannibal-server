package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.game.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService gameService;

    @Autowired
    LobbyService lobbyService;
    private static final Player PLAYER_1 = new Player(0, "Player 1", "token1", Team.RED);
    private static final Player PLAYER_2 = new Player(1, "Player 2", "token2", Team.BLUE);

    private static final String PLAYER1_TOKEN = "token1";
    private static final String PLAYER2_TOKEN = "token2";

    private static final Position OUT_OF_RANGE_POSITION = new Position(100000, 100000);
    private static final long NO_GAME_ID = 100000L;

    private static final long GAME_ID = 1L;
    private Position redUnitPosition;
    private Position blueUnitPosition;
    private Position noUnitPosition;

    private AttackCommand attackCommand;
    private MoveCommand moveCommand;

    @Mock
    ILobbyManager lobbyManager;

    @Mock
    ILobby lobbyWithGame;

    @Mock
    ILobby lobbyWithoutGame;


    private ILobbyManager oldLobbyManger;

    @BeforeEach
    void beforeEach() {
        Map<String, IPlayer> players = Map.of(PLAYER_1.getToken(), PLAYER_1, PLAYER_2.getToken(), PLAYER_2);
        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, players);
        when(lobbyManager.getLobbyWithId(GAME_ID)).thenReturn(lobbyWithGame);
        when(lobbyManager.getLobbyWithId(NO_GAME_ID)).thenReturn(lobbyWithoutGame);
        when(lobbyWithGame.getGame()).thenReturn(game);
        when(lobbyWithoutGame.getGame()).thenReturn(null);
        oldLobbyManger = gameService.setLobbyManager(lobbyManager);
        redUnitPosition = positionWithTeamUnit(game, Team.RED);
        blueUnitPosition = positionWithTeamUnit(game, Team.BLUE);
        noUnitPosition = positionWithNoUnit(game);
    }

    @AfterEach
    void cleanUp() {
        gameService.setLobbyManager(oldLobbyManger);
    }

    @Test
    void attack_NotPlayersTurnException() {
        attackCommand = new AttackCommand(redUnitPosition, blueUnitPosition, redUnitPosition);

        String token = PLAYER_2.getToken();
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, token, attackCommand));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void attack_TileOutOfRangeException() {
        attackCommand = new AttackCommand(redUnitPosition, OUT_OF_RANGE_POSITION, redUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER1_TOKEN, attackCommand));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void attack_NotAMemberOfGameException() {
        attackCommand = new AttackCommand(redUnitPosition, blueUnitPosition, redUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, "wrong token", attackCommand));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void attack_UnitNotFoundException() {
        attackCommand = new AttackCommand(noUnitPosition, blueUnitPosition, redUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER1_TOKEN, attackCommand));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void attack_GameNotFoundException() {
        attackCommand = new AttackCommand(redUnitPosition, blueUnitPosition, redUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(NO_GAME_ID, PLAYER1_TOKEN, attackCommand));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void attack_WrongUnitOwnerException() {
        attackCommand = new AttackCommand(blueUnitPosition, redUnitPosition, blueUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER1_TOKEN, attackCommand));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void attack_WrongTargetTeamException() {
        attackCommand = new AttackCommand(redUnitPosition, redUnitPosition, redUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER1_TOKEN, attackCommand));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    // TODO: Make this test work correctly at the moment it just uses that unit movement range is not checked
    @Test
    void attack_OK() {
        Position newPos = new Position(blueUnitPosition.getX()-1, blueUnitPosition.getY());
        attackCommand = new AttackCommand(redUnitPosition, blueUnitPosition, newPos);
        assertDoesNotThrow(() -> gameService.unitAttack(GAME_ID, PLAYER1_TOKEN, attackCommand));
    }

    // TODO test for TargetUnreachableException once it is implemented in units.
    @Test
    void wait_NotPlayersTurnException() {
        moveCommand = new MoveCommand(redUnitPosition, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER2_TOKEN, moveCommand));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void wait_TileOutOfRangeException() {
        moveCommand = new MoveCommand(OUT_OF_RANGE_POSITION, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER1_TOKEN, moveCommand));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void wait_NotAMemberOfGameException() {
        moveCommand = new MoveCommand(redUnitPosition, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, "wrong token", moveCommand));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void wait_UnitNotFoundException() {
        moveCommand = new MoveCommand(noUnitPosition, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER1_TOKEN, moveCommand));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void wait_GameNotFoundException() {
        moveCommand = new MoveCommand(redUnitPosition, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(NO_GAME_ID, PLAYER1_TOKEN, moveCommand));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void wait_WrongUnitOwnerException() {
        moveCommand = new MoveCommand(blueUnitPosition, noUnitPosition);
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER1_TOKEN, moveCommand));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void wait_OK() {
        moveCommand = new MoveCommand(redUnitPosition, noUnitPosition);
        assertDoesNotThrow(() -> gameService.unitMove(GAME_ID, PLAYER1_TOKEN, moveCommand));
    }

    @Test
    void validSurrender() {
        GameDelta gameDelta = gameService.surrender(GAME_ID, PLAYER1_TOKEN);

        GameDelta expected = new GameDelta(/*moveCommand=*/ null,
                /*unitHealths*/ null,
                new TurnInfo(/*turn=*/1, /*playerId=*/ 1),
                new GameOverInfo(List.of(1L), /*rankedScoreDeltas=*/ null),
                new SurrenderInfo(0L, /*rankedScoreDeltas=*/ null));

        assertEquals(expected, gameDelta);
    }

    private Position positionWithTeamUnit(Game game, Team team) {
        return game.getDecoratedPlayers().values().stream()
                .filter(player -> player.getTeam().equals(team)) // Get all players in team.
                .flatMap(player -> player.getUnits().stream()) // Get their units
                .map(Unit::getPosition) // Get their positions
                .findAny() //Find the first one
                .get();
    }

    private Position positionWithNoUnit(Game game) {
        Set<Position> occupiedPositions = game.getDecoratedPlayers().values().stream()//Get all players
                .flatMap(player -> player.getUnits().stream())//Get their units.
                .map(Unit::getPosition) // Get their positions
                .collect(Collectors.toSet()); // Store them in a set.
        List<List<Tile>> tiles = game.getGameMap().getTiles();
        for (int x = 0; x < tiles.size(); x++)
            for (int y = 0; y < tiles.get(x).size(); y++) {
                Position position = new Position(x, y);
                if (occupiedPositions.contains(position))
                    continue;
                return position;
            }
        throw new NoSuchElementException();
    }
}
