package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.interfaces.IMap;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private final GameMode gameMode;
    private final GameType gameType;
    private final Map<String, PlayerAdapter> playerMap;
    private final IMap map;
    private int turn;


    public Game(GameMode gameMode, GameType gameType, Map<String, Player> playerMap){
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.playerMap = new HashMap<>();

        // Convert players to PlayerAdapter's
        for(Player player: playerMap.values()){
            this.playerMap.put(player.getToken(), new PlayerAdapter(player));
        }

        // This should probably be static, MapFactory still in development
        map = new MapFactory().createMap();
    }

}
