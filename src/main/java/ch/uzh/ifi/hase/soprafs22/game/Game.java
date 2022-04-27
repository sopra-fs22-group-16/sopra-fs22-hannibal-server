package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;
import ch.uzh.ifi.hase.soprafs22.game.maps.MapLoader;
import ch.uzh.ifi.hase.soprafs22.game.maps.UnitsLoader;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.*;
import java.util.stream.Collectors;

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

        for(IPlayer player: playerMap.values()){
            int teamNumber = player.getTeam().ordinal();
            if(turnOrder[teamNumber] == null){
                turnOrder[teamNumber] = player.getToken();
            }else if( turnOrder.length > 2 && turnOrder[teamNumber+2] == null ){
                turnOrder[teamNumber+2] = player.getToken();
            }
        }

        List<Unit> unitList = new ArrayList<>();
        //TODO Potential Feature: RANKED games get a harder map
        if(gameType==GameType.UNRANKED||gameType==GameType.RANKED){
            this.gameMap = new MapLoader().deserialize("beginner_map.json");
            unitList = new UnitsLoader().deserialize("beginner_map.json");
        }

        // Convert players to PlayerDecorators
        for(IPlayer player: playerMap.values()){
            List<Unit> filteredUnitList = unitList.stream()
                    .filter(u -> u.getUserId() == player.getId()).collect(Collectors.toList());
            this.playerMap.put(player.getToken(), new PlayerDecorator(player, filteredUnitList));
        }
    }

    public Map<String, PlayerDecorator> getPlayerMap() {
        return playerMap;
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
        ensureWithinRange(attacker);
        ensureWithinRange(defender);
        Optional<Unit> attackingUnitOptional = getUnitAt(attacker);
        if (attackingUnitOptional.isEmpty())
            throw new UnitNotFoundException(attacker);
        Unit attackingUnit = attackingUnitOptional.get();
        Optional<Unit> defendingUnitOptional = getUnitAt(defender);
        if (defendingUnitOptional.isEmpty())
            throw new UnitNotFoundException(attacker);
        Unit defendingUnit = defendingUnitOptional.get();
        ensureUnitOwner(attackingUnit, token);
        ensureUnitEnemy(attackingUnit, defendingUnit);

        attackingUnit.attack(defendingUnit);
    }

    private void ensureWithinRange(Position position) throws TileOutOfRangeException {
        List<List<Tile>> tiles = gameMap.getTiles();
        // NOTE that X and Y are reversed in the tiles!! it is tiles[y][x], not tiles[x][y]
        int yRange = tiles.size();
        int xRange = tiles.get(0).size();
        if (position.getY() >= tiles.size())
            throw new TileOutOfRangeException(position, xRange, yRange);
        if (position.getX() >= tiles.get(position.getY()).size())
            throw new TileOutOfRangeException(position, xRange, yRange);
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
        ensureWithinRange(start);
        ensureWithinRange(end);
        Optional<Unit> movingUnitOptional = getUnitAt(start);
        if (movingUnitOptional.isEmpty())
            throw new UnitNotFoundException(start);
        Unit movingUnit = movingUnitOptional.get();
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
        ensureWithinRange(position);
        Optional<Unit> waitingUnitOptional = getUnitAt(position);
        if (waitingUnitOptional.isEmpty())
            throw new UnitNotFoundException(position);
        Unit waitingUnit = waitingUnitOptional.get();
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

    private Optional<Unit> getUnitAt(Position position) {
        return playerMap.values().stream()
                .flatMap(player -> player.getUnits().stream())
                .filter(unit -> unit.getPosition().equals(position))
                .findAny();
    }
}
