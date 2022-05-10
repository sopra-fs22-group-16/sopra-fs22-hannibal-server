package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import ch.uzh.ifi.hase.soprafs22.game.logger.GameLogger;
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
    private long playerIdCurrentTurn;
    private final Long[] turnOrder;
    private final boolean running;

    private final GameLogger gameLogger;


    public Game(GameMode gameMode, GameType gameType, Map<String, IPlayer> playerMap) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.playerMap = new HashMap<>();
        this.turnNumber = 0;
        this.turnOrder = new Long[playerMap.size()];
        this.running = true;

        for (IPlayer player : playerMap.values()) {
            int teamNumber = player.getTeam().ordinal();
            if (this.turnOrder[teamNumber] == null) {
                this.turnOrder[teamNumber] = player.getId();
            }
            else if (this.turnOrder.length > 2 && this.turnOrder[teamNumber + 2] == null) {
                this.turnOrder[teamNumber + 2] = player.getId();
            }
        }

        List<Unit> unitList = new ArrayList<>();
        //TODO Potential Feature: RANKED games get a harder map
        if (gameType == GameType.UNRANKED || gameType == GameType.RANKED) {
            this.gameMap = new MapLoader().deserialize("beginner_map.json");
            unitList = new UnitsLoader().deserialize("beginner_map.json");
        }

        // Convert players to PlayerDecorators
        for (IPlayer player : playerMap.values()) {
            List<Unit> filteredUnitList = unitList.stream()
                    .filter(u -> u.getUserId() == player.getId()).collect(Collectors.toList());
            PlayerDecorator playerDecorator = new PlayerDecorator(player, filteredUnitList);
            for (Unit u : filteredUnitList) {
                u.addObserver(playerDecorator);
            }
            this.playerMap.put(player.getToken(), playerDecorator);
        }

        this.playerIdCurrentTurn = this.turnOrder[this.turnNumber % this.turnOrder.length];
        Map<Long, Integer> numberOfUnitsPerPlayerId = this.playerMap.values().stream().
                collect(Collectors.toMap(PlayerDecorator::getId, player -> player.getUnits().size()));
        this.gameLogger = new GameLogger(numberOfUnitsPerPlayerId);
    }

    public Map<String, PlayerDecorator> getPlayerMap() {
        return this.playerMap;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public int getTurnNumber() {
        return this.turnNumber;
    }

    public long getPlayerIdCurrentTurn() {
        return this.playerIdCurrentTurn;
    }

    /**
     * Make sure that this info makes it to GameController *EVERY* time there is a next turn.
     * For example, if a movement ends the turn, this needs to be passed to GameController, if a player ends the turn,
     * this needs to be returned to GameController.
     *
     * @return The current TurnInfo
     */
    public TurnInfo nextTurn() {
        ++this.turnNumber;
        this.playerIdCurrentTurn = this.turnOrder[this.turnNumber % this.turnOrder.length];
        this.gameLogger.nextTurn();
        return new TurnInfo(this.turnNumber, this.playerIdCurrentTurn);
    }


    public boolean hasEnded() {
        return !this.running;
    }

    public boolean isPlayersTurn(String token) {
        return this.playerMap.get(token).getId() == this.turnOrder[this.turnNumber % this.turnOrder.length];
    }

    /**
     * Returns the units whose health got affected.
     */
    public List<Unit> unitAttack(String token, Position attacker, Position defender) throws NotPlayersTurnException,
            TileOutOfRangeException,
            AttackOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            WrongUnitOwnerException,
            WrongTargetTeamException {
        if (!this.playerMap.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
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
        if (this.playerMap.get(token).getId() != attackingUnit.getUserId())
            throw new WrongUnitOwnerException(attackingUnit, this.playerMap.get(token).getId());
        if (attackingUnit.getTeamId() == defendingUnit.getTeamId())
            throw new WrongTargetTeamException(attackingUnit, defendingUnit);
        attackingUnit.attack(defendingUnit);
        // TODO: attacking does not move the unit (setPosition), but interface implies it does!
        // logger.move(turnNumber);
        if (defendingUnit.getHealth() <= 0)
            gameLogger.unitKilledAtTurn(turnNumber, defendingUnit.getUserId());
        if (attackingUnit.getHealth() <= 0)
            gameLogger.unitKilledAtTurn(turnNumber, attackingUnit.getUserId());
        return List.of(defendingUnit, attackingUnit);
    }

    private void ensureWithinRange(Position position) throws TileOutOfRangeException {
        List<List<Tile>> tiles = this.gameMap.getTiles();
        // NOTE that X and Y are reversed in the tiles!! it is tiles[y][x], not tiles[x][y]
        int yRange = tiles.size();
        int xRange = tiles.get(0).size();
        if (position.getY() >= tiles.size())
            throw new TileOutOfRangeException(position, xRange, yRange);
        if (position.getX() >= tiles.get(position.getY()).size())
            throw new TileOutOfRangeException(position, xRange, yRange);
    }

    public void unitWait(String token, Position start, Position end) throws NotPlayersTurnException,
            TileOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            TargetUnreachableException,
            WrongUnitOwnerException {
        if (!this.playerMap.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
        ensureWithinRange(start);
        ensureWithinRange(end);
        Optional<Unit> movingUnitOptional = getUnitAt(start);
        if (movingUnitOptional.isEmpty())
            throw new UnitNotFoundException(start);
        Unit movingUnit = movingUnitOptional.get();
        if (this.playerMap.get(token).getId() != movingUnit.getUserId())
            throw new WrongUnitOwnerException(movingUnit, this.playerMap.get(token).getId());
        movingUnit.setPosition(end);
        if (!start.equals(end))
            gameLogger.move(turnNumber);
    }

    public IGameStatistics getStatistics() {
        return this.gameLogger;
    }

    private Optional<Unit> getUnitAt(Position position) {
        return this.playerMap.values().stream()
                .flatMap(player -> player.getUnits().stream())
                .filter(unit -> unit.getPosition().equals(position))
                .findAny();
    }
}
