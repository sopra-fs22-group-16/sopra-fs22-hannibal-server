package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;

public class GameDelta {
    private MoveCommand moveCommand = null;
    private Map<Position, Integer> unitHealths = null;
    private TurnInfo turnInfo = null;

    private GameOverInfo gameOverInfo = null;

    private SurrenderInfo surrenderInfo = null;

    public GameDelta() { }

    public GameDelta setMoveCommand(MoveCommand moveCommand) {
        this.moveCommand = moveCommand;
        return this;
    }

    public GameDelta setUnitHealths(Map<Position, Integer> unitHealths) {
        this.unitHealths = unitHealths;
        return this;
    }
    public GameDelta setTurnInfo(TurnInfo turnInfo) {
        this.turnInfo = turnInfo;
        return this;
    }

    public GameDelta setGameOverInfo(GameOverInfo gameOverInfo) {
        this.gameOverInfo = gameOverInfo;
        return this;
    }

    public GameDelta setSurrenderInfo(SurrenderInfo surrenderInfo) {
        this.surrenderInfo = surrenderInfo;
        return this;
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
}
