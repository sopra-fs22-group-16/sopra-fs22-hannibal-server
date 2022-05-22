package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Map<String, IPlayer> playerMap;
    private Game game;
    Position redUnitPosition;
    Position blueUnitPosition;
    Position noUnitPosition;

    @BeforeEach
    void setUp() {
        playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);
        redUnitPosition = positionWithTeamUnit(game, Team.RED);
        blueUnitPosition = positionWithTeamUnit(game, Team.BLUE);
        noUnitPosition = positionWithNoUnit(game);
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

    @Test
    void gameRunningAtStart() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);

        assertFalse(game.hasEnded());
    }

    @Test
    void nextTurn_1v1_nextTurn() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);

        TurnInfo turn = game.nextTurn();

        assertEquals(1, turn.getTurn()); // Games start at turn 0, not turn 1.
        assertEquals(1, turn.getPlayerId());
        assertTrue(game.isPlayersTurn("token1"));
    }

    @Test
    void nextTurn_1v1_continuous() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);

        TurnInfo turn1 = game.nextTurn();
        TurnInfo turn2 = game.nextTurn();

        assertEquals(1, turn1.getTurn());
        assertEquals(1, turn1.getPlayerId());
        assertEquals(2, turn2.getTurn());
        assertEquals(0, turn2.getPlayerId());
        assertTrue(game.isPlayersTurn("token0"));
    }

    @Test
    void isPlayersTurn_true() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);

        assertTrue(game.isPlayersTurn("token0"));
    }

    @Test
    void isPlayersTurn_false() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);

        assertFalse(game.isPlayersTurn("token1"));
    }

    // TODO test GameOverException for attack, move and wait.
    // TODO test AttackOutOfRangeException for attack.

    @Test
    void attack_nonMember_throwsNotAMemberOfGameException() {
        assertThrows(NotAMemberOfGameException.class, () -> game.unitAttack("badToken", redUnitPosition, blueUnitPosition));
    }

    @Test
    void attack_noTurn_throwsNotPlayersTurnException() {
        assertThrows(NotPlayersTurnException.class, () -> game.unitAttack("token1", redUnitPosition, blueUnitPosition));
    }

    @Test
    void attack_nonAttacker_throwsUnitNotFoundException() {
        assertThrows(UnitNotFoundException.class, () -> game.unitAttack("token0", noUnitPosition, blueUnitPosition));
    }

    @Test
    void attack_noDefender_throwsUnitNotFoundException() {
        assertThrows(UnitNotFoundException.class, () -> game.unitAttack("token0", redUnitPosition, noUnitPosition));
    }

    @Test
    void attack_notOwner_throwsWrongUnitOwnerException() {
        assertThrows(WrongUnitOwnerException.class, () -> game.unitAttack("token0", blueUnitPosition, redUnitPosition));
    }

    @Test
    void attack_notEnemy_throwsWrongTargetTeamException() {
        assertThrows(WrongTargetTeamException.class, () -> game.unitAttack("token0", redUnitPosition, redUnitPosition));
    }

    @Test
    void attack_good() throws Exception {
        game.unitAttack("token0", redUnitPosition, blueUnitPosition);
    }

    // TODO test TargetUnreachableException for move.
    @Test
    void wait_nonMember_throwsNotAMemberOfGameException() {
        assertThrows(NotAMemberOfGameException.class, () -> game.unitMove("badToken", redUnitPosition, blueUnitPosition));
    }

    @Test
    void wait_noTurn_throwsNotPlayersTurnException() {
        assertThrows(NotPlayersTurnException.class, () -> game.unitMove("token1", redUnitPosition, blueUnitPosition));
    }

    @Test
    void wait_nonUnit_throwsUnitNotFoundException() {
        assertThrows(UnitNotFoundException.class, () -> game.unitMove("token0", noUnitPosition, blueUnitPosition));
    }

    @Test
    void wait_notOwner_throwsWrongUnitOwnerException() {
        assertThrows(WrongUnitOwnerException.class, () -> game.unitMove("token0", blueUnitPosition, redUnitPosition));
    }

    @Test
    void wait_good() throws Exception {
        game.unitMove("token0", redUnitPosition, noUnitPosition);
    }

    @Test
    void test_surrender() throws Exception {
        playerMap.put("token2", new Player(2L, "user2", "token2", Team.RED));
        playerMap.put("token3", new Player(3L, "user3", "token3", Team.BLUE));
        game = new Game(GameMode.TWO_VS_TWO, GameType.UNRANKED, playerMap);

        long surrenderId = game.surrender("token0");

        assertEquals(0L, surrenderId);
    }

    @Test
    void test_NextTurn() throws Exception {
        playerMap.put("token2", new Player(2L, "user2", "token2", Team.RED));
        playerMap.put("token3", new Player(3L, "user3", "token3", Team.BLUE));
        game = new Game(GameMode.TWO_VS_TWO, GameType.UNRANKED, playerMap);
        game.surrender("token0"); // player 0 surrenders, turn = 0
        game.nextTurn(); // player 0 ends turn (does not happen in game, service does it!)
        game.nextTurn(); // player 1 skips turn, turn = 1
        game.nextTurn(); // player 2 skips turn, turn = 2

        // Get the turn for player after 3 (who surrendered at the start!)
        TurnInfo turnInfo = game.nextTurn(); // player 3 skips turn, turn = 3

        assertEquals(1L, turnInfo.getPlayerId());
        assertEquals(4, turnInfo.getTurn());
    }

}