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
    private final Map<String, PlayerDecorator> decoratedPlayers;
    private GameMap gameMap;
    private int turnNumber;
    private long playerIdCurrentTurn;
    private final Long[] turnOrder;
    private final boolean running;

    private final GameLogger gameLogger;


    public Game(GameMode gameMode, GameType gameType, Map<String, IPlayer> decoratedPlayers) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.decoratedPlayers = new HashMap<>();
        this.turnNumber = 0;
        this.turnOrder = new Long[decoratedPlayers.size()];
        this.running = true;

        for (IPlayer player : decoratedPlayers.values()) {
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
        for (IPlayer player : decoratedPlayers.values()) {
            List<Unit> filteredUnitList = unitList.stream()
                    .filter(u -> u.getUserId() == player.getId()).collect(Collectors.toList());
            PlayerDecorator playerDecorator = new PlayerDecorator(player, filteredUnitList);
            for (Unit u : filteredUnitList) {
                u.addObserver(playerDecorator);
            }
            this.decoratedPlayers.put(player.getToken(), playerDecorator);
        }

        this.playerIdCurrentTurn = this.turnOrder[this.turnNumber % this.turnOrder.length];
        Map<Long, Integer> numberOfUnitsPerPlayerId = this.decoratedPlayers.values().stream().
                collect(Collectors.toMap(PlayerDecorator::getId, player -> player.getUnits().size()));
        this.gameLogger = new GameLogger(numberOfUnitsPerPlayerId);
    }

    public Map<String, PlayerDecorator> getDecoratedPlayers() {
        return this.decoratedPlayers;
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

    private boolean resetUnitsFromPreviousTurn(String token) {
        return this.decoratedPlayers.get(token).resetUnitsMovedStatus();
    }

    public boolean hasEnded() {
        return !this.running;
    }

    public boolean isPlayersTurn(String token) {
        return this.decoratedPlayers.get(token).getId() == this.turnOrder[this.turnNumber % this.turnOrder.length];
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
        if (!this.decoratedPlayers.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
        ensureWithinRange(attacker);
        ensureWithinRange(defender);
        Unit attackingUnit = getUnitAtPosition(attacker);
        if (attackingUnit == null)
            throw new UnitNotFoundException(attacker);
        Unit defendingUnit = getUnitAtPosition(defender);
        if (defendingUnit == null)
            throw new UnitNotFoundException(defender);
        if (this.decoratedPlayers.get(token).getId() != attackingUnit.getUserId())
            throw new WrongUnitOwnerException(attackingUnit, this.decoratedPlayers.get(token).getId());
        if (attackingUnit.getTeamId() == defendingUnit.getTeamId())
            throw new WrongTargetTeamException(attackingUnit, defendingUnit);
        attackingUnit.attack(defendingUnit);
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

    public Unit unitMove(String token, Position start, Position end) throws NotPlayersTurnException,
            TileOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            TargetUnreachableException,
            WrongUnitOwnerException {
        if (!this.decoratedPlayers.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
        ensureWithinRange(start);
        ensureWithinRange(end);
        Unit movingUnit = getUnitAtPosition(start);
        if (movingUnit == null) {
            throw new UnitNotFoundException(start);
        }
        if (this.decoratedPlayers.get(token).getId() != movingUnit.getUserId()) {
            throw new WrongUnitOwnerException(movingUnit, this.decoratedPlayers.get(token).getId());
        }
        movingUnit.setPosition(end);
        if (!start.equals(end))
            gameLogger.move(turnNumber);
        movingUnit.setMoved(true);
        if (haveAllUnitsOfPlayerMoved(token)) {
            boolean allUnitsMovedStatusReset = resetUnitsFromPreviousTurn(token);
            if (allUnitsMovedStatusReset) {
                nextTurn();
            }
        }
        return movingUnit;
    }

    private boolean haveAllUnitsOfPlayerMoved(String token) {
        return this.decoratedPlayers.get(token).getUnits().stream().allMatch(u -> u.getMoved() == true);
    }


    private Unit getUnitAtPosition(Position position) {
        return this.decoratedPlayers.values().stream()
                .flatMap(player -> player.getUnits().stream())
                .filter(unit -> unit.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    public IGameStatistics getStatistics() {
        return this.gameLogger;
    }
}
