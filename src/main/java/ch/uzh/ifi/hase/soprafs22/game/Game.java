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
    private int turnNumber;
    private String[] turnOrder;
    private boolean running;


    public Game(GameMode gameMode, GameType gameType, Map<String, Player> playerMap){
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.playerMap = new HashMap<>();
        this.turnNumber = 0;
        this.turnOrder = new String[playerMap.size()];
        this.running = true;

        // Convert players to PlayerAdapter's
        for(Player player: playerMap.values()){
            this.playerMap.put(player.getToken(), new PlayerAdapter(player));
        }

        for(Player player: playerMap.values()){
            int teamNumber = player.getTeam().getTeamNumber();
            if(turnOrder[teamNumber] == null){
                turnOrder[teamNumber] = player.getToken();
            }else{
                turnOrder[teamNumber+2] = player.getToken();
            }
        }

        // TODO: Update with correct call
        map = new MapFactory().createMap();

    }

    public int nextTurn(){
        return ++turnNumber;
    }

    public boolean hasEnded(){
        return !running;
    }

    public boolean isPlayersTurn(String token){
        return turnOrder[turnNumber % turnOrder.length].equals(token);
    }
}
