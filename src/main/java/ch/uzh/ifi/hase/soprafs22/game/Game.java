package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;
import ch.uzh.ifi.hase.soprafs22.game.maps.MapLoader;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

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


    public Game(GameMode gameMode, GameType gameType, Map<String, IPlayer> playerMap){
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
            }
        }

        //TODO Potential Feature: RANKED games get a harder map
        if(gameType==GameType.UNRANKED||gameType==GameType.RANKED){
            this.gameMap = new MapLoader().deserialize("beginner_map.json");
        }

    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameType getGameType() {
        return gameType;
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

    public void unitAttack(String token, Position attacker, Position defender) throws NotPlayersTurnException,
            TileOutOfRangeException,
            AttackOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            WrongUnitOwnerException,
            WrongTargetTeamException {
        ensureMember(token);
        ensureNotEnded();
        ensureTurn(token);
        Unit attackingUnit = gameMap.getTile(attacker).getUnit();
        if (attackingUnit == null)
            throw new UnitNotFoundException(attacker);
        Unit defendingUnit = gameMap.getTile(defender).getUnit();
        if (defendingUnit == null)
            throw new UnitNotFoundException(attacker);
        ensureUnitOwner(attackingUnit, token);
        ensureUnitEnemy(attackingUnit, defendingUnit);

        attackingUnit.attack(defendingUnit);
    }

    public void unitMove(String token, Position start, Position end) throws NotPlayersTurnException,
            TileOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            TargetUnreachableException,
            WrongUnitOwnerException {
        ensureMember(token);
        ensureNotEnded();
        ensureTurn(token);
        Unit movingUnit = gameMap.getTile(start).getUnit();
        if (movingUnit == null)
            throw new UnitNotFoundException(start);
        ensureUnitOwner(movingUnit, token);
        movingUnit.move(start, end);
    }

    public void unitWait(String token, Position position) throws NotPlayersTurnException,
            TileOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            WrongUnitOwnerException {
        ensureMember(token);
        ensureNotEnded();
        ensureTurn(token);
        Unit waitingUnit = gameMap.getTile(position).getUnit();
        if (waitingUnit == null)
            throw new UnitNotFoundException(position);
        ensureUnitOwner(waitingUnit, token);
        waitingUnit.unitWait();
    }

    private void ensureMember(String token) throws NotAMemberOfGameException {
        if (!playerMap.containsKey(token))
            throw new NotAMemberOfGameException();
    }

    private void ensureNotEnded() throws GameOverException {
        if (hasEnded())
            throw new GameOverException();
    }

    private void ensureTurn(String token) throws NotPlayersTurnException {
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
    }

    private void ensureUnitOwner(Unit unit, String token) throws WrongUnitOwnerException {
        if (playerMap.get(token).getId() != unit.getUserId())
            throw new WrongUnitOwnerException(unit, playerMap.get(token).getId());
    }

    private void ensureUnitEnemy(Unit first, Unit second) throws WrongTargetTeamException {
        if (first.getTeamId() == second.getTeamId())
            throw new WrongTargetTeamException(first, second);
    }
}
