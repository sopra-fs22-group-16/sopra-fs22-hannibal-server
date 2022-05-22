package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import ch.uzh.ifi.hase.soprafs22.game.logger.GameLogger;
import ch.uzh.ifi.hase.soprafs22.game.maps.GameMap;
import ch.uzh.ifi.hase.soprafs22.game.maps.MapLoader;
import ch.uzh.ifi.hase.soprafs22.game.maps.UnitsLoader;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.uzh.ifi.hase.soprafs22.game.enums.Team.BLUE;
import static ch.uzh.ifi.hase.soprafs22.game.enums.Team.RED;

public class Game {
    private final GameMode gameMode;
    private final GameType gameType;
    private final Map<String, PlayerDecorator> decoratedPlayers;
    private final Turn turn;
    private GameMap gameMap;
    private boolean running;

    private final GameLogger gameLogger;


    public Game(GameMode gameMode, GameType gameType, Map<String, IPlayer> decoratedPlayers) {
        this.gameMode = gameMode;
        this.gameType = gameType;
        this.decoratedPlayers = new HashMap<>();
        this.running = true;

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
                u.registerObserver(playerDecorator);
            }
            this.decoratedPlayers.put(player.getToken(), playerDecorator);
        }
        this.turn = new Turn(this.decoratedPlayers.values());
        Map<Long, Integer> numberOfUnitsPerPlayerId = this.decoratedPlayers.values().stream().
                collect(Collectors.toMap(PlayerDecorator::getId, player -> player.getUnits().size()));
        this.gameLogger = new GameLogger(numberOfUnitsPerPlayerId);
    }

    public Map<String, PlayerDecorator> getDecoratedPlayers() {
        return decoratedPlayers;
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

    public int getTurnNumber() {
        return this.turn.getTurnNumber();
    }

    public long getPlayerIdCurrentTurn() {
        return turn.getPlayerId();
    }

    public TurnInfo nextTurn() {
        this.gameLogger.nextTurn();
        return this.turn.nextTurn();
    }

    private boolean resetUnitsFromPreviousTurn(String token) {
        return this.decoratedPlayers.get(token).resetUnitsMovedStatus();
    }

    public boolean hasEnded() {
        return !this.running;
    }

    public boolean isPlayersTurn(String token) {
        return turn.getPlayerId() == this.decoratedPlayers.get(token).getId();
    }

    /**
     * Returns the units whose health got affected.
     */
    public GameDelta unitAttack(String token, Position attacker, Position attackerDestination, Position defender) throws NotPlayersTurnException,
            TileOutOfRangeException,
            AttackOutOfRangeException,
            NotAMemberOfGameException,
            GameOverException,
            UnitNotFoundException,
            WrongUnitOwnerException,
            WrongTargetTeamException, TargetUnreachableException {
        if (!this.decoratedPlayers.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token)) {
            throw new NotPlayersTurnException();
        }
        ensureWithinRange(attacker);
        ensureWithinRange(attackerDestination);
        ensureWithinRange(defender);
        Unit attackingUnit = getUnitAtPosition(attacker);
        if (attackingUnit == null)
            throw new UnitNotFoundException(attacker);
        if (this.decoratedPlayers.get(token).getId() != attackingUnit.getUserId()) {
            throw new WrongUnitOwnerException(attackingUnit, this.decoratedPlayers.get(token).getId());
        }
        attackingUnit.setPosition(attackerDestination);
        if (!attacker.equals(attackerDestination))
            gameLogger.move(turn.getTurnNumber());
        attackingUnit.setMoved(true);
        Unit defendingUnit = getUnitAtPosition(defender);
        if (defendingUnit == null)
            throw new UnitNotFoundException(defender);
        if (this.decoratedPlayers.get(token).getId() != attackingUnit.getUserId())
            throw new WrongUnitOwnerException(attackingUnit, this.decoratedPlayers.get(token).getId());
        if (attackingUnit.getTeamId() == defendingUnit.getTeamId())
            throw new WrongTargetTeamException(attackingUnit, defendingUnit);

        attackingUnit.attack(defendingUnit);

        if (defendingUnit.getHealth() <= 0)
            gameLogger.unitKilledAtTurn(turn.getTurnNumber(), attackingUnit.getUserId(), defendingUnit.getUserId());
        if (attackingUnit.getHealth() <= 0)
            gameLogger.unitKilledAtTurn(turn.getTurnNumber(), defendingUnit.getUserId(), attackingUnit.getUserId());
        checkGameOver();
        MoveCommand moveCommand = new MoveCommand(attacker, attackerDestination);
        Map<Position, Integer> unitHealths = Map.of(defendingUnit.getPosition(), defendingUnit.getHealth(), attackingUnit.getPosition(), attackingUnit.getHealth());
        return new GameDelta(moveCommand, unitHealths, checkNextTurn(token), getGameOverInfo());
    }

    public GameDelta unitMove(String token, Position start, Position end) throws NotPlayersTurnException,
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
            gameLogger.move(turn.getTurnNumber());
        movingUnit.setMoved(true);
        MoveCommand executedMove = new MoveCommand(start, movingUnit.getPosition());
        return new GameDelta(executedMove, checkNextTurn(token), getGameOverInfo());
    }

    public GameDelta surrender(String token) throws NotAMemberOfGameException, GameOverException, NotPlayersTurnException {
        if (!this.decoratedPlayers.containsKey(token))
            throw new NotAMemberOfGameException();
        if (hasEnded())
            throw new GameOverException();
        if (!isPlayersTurn(token))
            throw new NotPlayersTurnException();
        PlayerDecorator player = decoratedPlayers.get(token);
        player.surrender();
        checkGameOver();
        SurrenderInfo surrenderInfo = new SurrenderInfo(player.getId());
        return new GameDelta(checkNextTurn(token), getGameOverInfo(), surrenderInfo);
    }

    private TurnInfo checkNextTurn(String token){
        return haveAllUnitsOfPlayerMoved(token) && resetUnitsFromPreviousTurn(token) ? nextTurn() : null;
    }

    private void checkGameOver() {
        boolean redUnitAlive = getAllPlayersThat(player -> player.getTeam() == RED)
                .flatMap(player -> player.getUnits().stream())
                .anyMatch(unit -> unit.getHealth() > 0);
        boolean blueUnitAlive = getAllPlayersThat(player -> player.getTeam() == BLUE)
                .flatMap(player -> player.getUnits().stream())
                .anyMatch(unit -> unit.getHealth() > 0);
        running = blueUnitAlive  && redUnitAlive;
    }

    private boolean haveAllUnitsOfPlayerMoved(String token) {
        return this.decoratedPlayers.get(token).getUnits().stream().allMatch(Unit::getMoved);
    }

    public IGameStatistics getStatistics() {
        return gameLogger;
    }

    private Unit getUnitAtPosition(Position position) {
        return this.decoratedPlayers.values().stream()
                .flatMap(player -> player.getUnits().stream())
                .filter(unit -> unit.getPosition().equals(position))
                .findFirst()
                .orElse(null);
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

    private GameOverInfo getGameOverInfo() {
        if (running)
            return null;
        Optional<PlayerDecorator> playerWithUnits = getAllPlayersThat(player -> player.getUnits().size() > 0).findFirst();
        Team winnerTeam = playerWithUnits.get().getTeam();
        List<Long> winners = getAllPlayersThat(player -> player.getTeam().equals(winnerTeam)).map(PlayerDecorator::getId).collect(Collectors.toList());
        return new GameOverInfo(winners);
    }

    private Stream<PlayerDecorator> getAllPlayersThat(Predicate<PlayerDecorator> predicate) {
        return this.decoratedPlayers.values().stream().filter(predicate);
    }
}
