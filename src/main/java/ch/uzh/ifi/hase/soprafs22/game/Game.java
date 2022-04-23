package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.UnbalancedTeamCompositionException;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;
import ch.uzh.ifi.hase.soprafs22.game.maps.MapLoader;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private final GameMode gameMode;
    private final GameType gameType;
    private final Map<String, PlayerDecorator> playerMap;
    private GameMap gameMap;
    private int turnNumber;
    private String[] turnOrder;
    private boolean running;


    public Game(GameMode gameMode, GameType gameType, Map<String, IPlayer> playerMap) throws UnbalancedTeamCompositionException {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.playerMap = new HashMap<>();
        this.turnNumber = 0;
        this.turnOrder = new String[playerMap.size()];
        this.running = true;

        // Convert players to PlayerDecorators
        for(IPlayer player: playerMap.values()){
            this.playerMap.put(player.getToken(), new PlayerDecorator(player));
        }

        for(IPlayer player: playerMap.values()){
            int teamNumber = player.getTeam().getTeamNumber();
            if(turnOrder[teamNumber] == null){
                turnOrder[teamNumber] = player.getToken();
            }else if( turnOrder.length > 2 && turnOrder[teamNumber+2] == null ){
                turnOrder[teamNumber+2] = player.getToken();
            }else{
                throw new UnbalancedTeamCompositionException();
            }
        }

        //TODO Potential Feature: RANKED games get a harder map
        if(gameType==GameType.UNRANKED||gameType==GameType.RANKED){
            this.gameMap = new MapLoader().deserialize("beginner_map.json");
        }

    }
    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
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
