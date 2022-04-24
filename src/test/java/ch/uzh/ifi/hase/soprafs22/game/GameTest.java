package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

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
        game.nextTurn();

        assertTrue(game.isPlayersTurn("token1"));
    }

    @Test
    void nextTurn_1v1_continuous() {
        Map<String, IPlayer> playerMap = new HashMap<>();
        playerMap.put("token0", new Player(0L, "user0", "token0", Team.RED));
        playerMap.put("token1", new Player(1L, "user1", "token1", Team.BLUE));

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED, playerMap);
        game.nextTurn();
        game.nextTurn();

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

}