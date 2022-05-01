package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@SpringBootTest
class GameServiceIntegrationTest {
    @Autowired
    GameService gameService;

    @Autowired
    LobbyService lobbyService;
    private static final Player PLAYER_1 = new Player(0, "Player 1", "token1", Team.RED);
    private static final Player PLAYER_2 = new Player(1, "Player 2", "token2", Team.BLUE);

    private static final Position OUT_OF_RANGE_POSITION = new Position(100000, 100000);
    private static final long NO_GAME_ID = 100000L;

    private static final long GAME_ID = 1L;
    private Position redUnitPosition;
    private Position blueUnitPosition;
    private Position noUnitPosition;
    @Mock
    ILobbyManager lobbyManager;

    @Mock
    ILobby lobbyWithGame;

    @Mock
    ILobby lobbyWithoutGame;


    @BeforeEach
    void setup() {
        Map<String, IPlayer> players = Map.of(PLAYER_1.getToken(), PLAYER_1, PLAYER_2.getToken(), PLAYER_2);
        Game game = new Game(GameMode.ONE_VS_ONE, GameType.RANKED, players);
        when(lobbyManager.getLobbyWithId(GAME_ID)).thenReturn(lobbyWithGame);
        when(lobbyManager.getLobbyWithId(NO_GAME_ID)).thenReturn(lobbyWithoutGame);
        when(lobbyWithGame.getGame()).thenReturn(game);
        when(lobbyWithoutGame.getGame()).thenReturn(null);

        gameService.setLobbyManager(lobbyManager);
        redUnitPosition = positionWithTeamUnit(game, Team.RED);
        blueUnitPosition = positionWithTeamUnit(game, Team.BLUE);
        noUnitPosition = positionWithNoUnit(game);
    }

    // TODO: add test for GameOver in attack, move and wait.
    // TODO test for AttackOutOfRangeException once it is implemented in units.
    @Test
    void attack_NotPlayersTurnException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER_2.getToken(), redUnitPosition, blueUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void attack_TileOutOfRangeException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER_1.getToken(), redUnitPosition, OUT_OF_RANGE_POSITION));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void attack_NotAMemberOfGameException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, "wrong token", redUnitPosition, blueUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void attack_UnitNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER_1.getToken(), noUnitPosition, blueUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void attack_GameNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(NO_GAME_ID, PLAYER_1.getToken(), redUnitPosition, blueUnitPosition));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void attack_WrongUnitOwnerException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER_1.getToken(), blueUnitPosition, redUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void attack_WrongTargetTeamException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitAttack(GAME_ID, PLAYER_1.getToken(), redUnitPosition, redUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void attack_OK() {
        gameService.unitAttack(GAME_ID, PLAYER_1.getToken(), redUnitPosition, blueUnitPosition);
    }

    // TODO test for TargetUnreachableException once it is implemented in units.
    @Test
    void wait_NotPlayersTurnException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_2.getToken(), redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void wait_TileOutOfRangeException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), OUT_OF_RANGE_POSITION, noUnitPosition));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void wait_NotAMemberOfGameException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, "wrong token", redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void wait_UnitNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), noUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void wait_GameNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(NO_GAME_ID, PLAYER_1.getToken(), redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void wait_WrongUnitOwnerException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), blueUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void wait_OK() {
        gameService.unitWait(GAME_ID, PLAYER_1.getToken(), redUnitPosition, noUnitPosition);
    }

    private Position positionWithTeamUnit(Game game, Team team) {
        return game.getPlayerMap().values().stream()
                .filter(player -> player.getTeam().equals(team)) // Get all players in team.
                .flatMap(player -> player.getUnits().stream()) // Get their units
                .map(Unit::getPosition) // Get their positions
                .findAny() //Find the first one
                .get();
    }

    private Position positionWithNoUnit(Game game) {
        Set<Position> occupiedPositions = game.getPlayerMap().values().stream()//Get all players
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
