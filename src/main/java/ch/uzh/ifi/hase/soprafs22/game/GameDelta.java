package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;
import java.util.Objects;

public class GameDelta {
    private final MoveCommand moveCommand;
    private final Map<Position, Integer> unitHealths;
    private final TurnInfo turnInfo;
    private final GameOverInfo gameOverInfo;
    private final SurrenderInfo surrenderInfo;

    public GameDelta(MoveCommand moveCommand,
                     Map<Position, Integer> unitHealths,
                     TurnInfo turnInfo,
                     GameOverInfo gameOverInfo,
                     SurrenderInfo surrenderInfo) {
        this.moveCommand = moveCommand;
        this.unitHealths = unitHealths;
        this.turnInfo = turnInfo;
        this.gameOverInfo = gameOverInfo;
        this.surrenderInfo = surrenderInfo;
    }

    public GameDelta(MoveCommand moveCommand, Map<Position, Integer> unitHealths, TurnInfo turnInfo, GameOverInfo gameOverInfo) {
        this(moveCommand, unitHealths, turnInfo, gameOverInfo, /*surrenderInfo=*/ null);
    }

    public GameDelta(MoveCommand executedMove, TurnInfo turnInfo, GameOverInfo gameOverInfo) {
        this(executedMove, /*unitHealths=*/ null, turnInfo, gameOverInfo,/*surrenderInfo=*/ null);
    }

    public GameDelta(TurnInfo turnInfo, GameOverInfo gameOverInfo, SurrenderInfo surrenderInfo) {
        this(/*moveCommand=*/ null, /*unitHealths=*/ null, turnInfo, gameOverInfo, surrenderInfo);
    }

    public MoveCommand getMoveCommand() {
        return moveCommand;
    }

    public Map<Position, Integer> getUnitHealths() {
        return unitHealths;
    }

    public TurnInfo getTurnInfo() {
        return turnInfo;
    }

    public GameOverInfo getGameOverInfo() {
        return gameOverInfo;
    }

    public SurrenderInfo getSurrenderInfo() {
        return surrenderInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDelta gameDelta = (GameDelta) o;
        return Objects.equals(moveCommand, gameDelta.moveCommand) && Objects.equals(unitHealths, gameDelta.unitHealths) && Objects.equals(turnInfo, gameDelta.turnInfo) && Objects.equals(gameOverInfo, gameDelta.gameOverInfo) && Objects.equals(surrenderInfo, gameDelta.surrenderInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveCommand, unitHealths, turnInfo, gameOverInfo, surrenderInfo);
    }

    @Override
    public String toString() {
        return "GameDelta{" +
                "moveCommand=" + moveCommand +
                ", unitHealths=" + unitHealths +
                ", turnInfo=" + turnInfo +
                ", gameOverInfo=" + gameOverInfo +
                ", surrenderInfo=" + surrenderInfo +
                '}';
    }
}
