package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {
    @Autowired
    GameService gameService;
    private static final Player PLAYER_1 = new Player(0, "Player 1", "token1", Team.RED);
    private static final Player PLAYER_2 = new Player(1, "Player 2", "token2", Team.BLUE);

    private static final Position OUT_OF_RANGE_POSITION = new Position(100000, 100000);
    private static final long NO_GAME_ID = 100000L;

    private static final long GAME_ID = 1L;
    private Position redUnitPosition;
    private Position blueUnitPosition;
    private Position noUnitPosition;

    @BeforeEach
    void setup() {
        Map<String, IPlayer> players = Map.of(PLAYER_1.getToken(), PLAYER_1, PLAYER_2.getToken(), PLAYER_2);
        Game game = new Game(GameMode.ONE_VS_ONE, GameType.RANKED, players);
        gameService.clear();
        gameService.addGame(GAME_ID, game);
        redUnitPosition = findMatchingPosition(game, tile -> tile.getUnit() != null  && tile.getUnit().getUserId()==PLAYER_1.getId());
        blueUnitPosition = findMatchingPosition(game, tile -> tile.getUnit() != null  && tile.getUnit().getUserId()==PLAYER_2.getId());
        noUnitPosition = findMatchingPosition(game, tile -> tile.getUnit() == null);
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
    void move_NotPlayersTurnException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER_2.getToken(), redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void move_TileOutOfRangeException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER_1.getToken(), OUT_OF_RANGE_POSITION, noUnitPosition));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void move_NotAMemberOfGameException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, "wrong token", redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void move_UnitNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER_1.getToken(), noUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void move_GameNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(NO_GAME_ID, PLAYER_1.getToken(), redUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void move_WrongUnitOwnerException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitMove(GAME_ID, PLAYER_1.getToken(), blueUnitPosition, noUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void move_OK() {
        gameService.unitMove(GAME_ID, PLAYER_1.getToken(), redUnitPosition, noUnitPosition);
    }

    @Test
    void unitWait_NotPlayersTurnException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_2.getToken(), redUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void unitWait_TileOutOfRangeException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), OUT_OF_RANGE_POSITION));

        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
    }

    @Test
    void unitWait_NotAMemberOfGameException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, "bad token", redUnitPosition));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void unitWait_UnitNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), noUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }
    @Test
    void GameNotFoundException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(NO_GAME_ID, PLAYER_1.getToken(), redUnitPosition));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void unitWait_WrongUnitOwnerException() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.unitWait(GAME_ID, PLAYER_1.getToken(), blueUnitPosition));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }
    @Test
    void unitWait_OK() {
        gameService.unitWait(GAME_ID, PLAYER_1.getToken(), redUnitPosition);
    }

    /**
     * Helper method in order to find positions with the search criteria.
     */
    private static final Position findMatchingPosition(Game game, Predicate<Tile> predicate) {
        List<List<Tile>> tiles = game.getGameMap().getTiles();
        for (int x = 0; x < tiles.size(); x++) {
            for (int y = 0; y < tiles.get(x).size(); y++){
                if (predicate.test(tiles.get(x).get(y)))
                    return new Position(x,y);
            }
        }
        throw new RuntimeException("Could not find position with criteria");
    }
}
