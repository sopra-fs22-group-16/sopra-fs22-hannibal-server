package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;

public class GameDelta {
    private MoveCommand moveCommand;
    private Map<Position, Integer> unitHealths;
    private final TurnInfo turnInfo;
    private final GameOverInfo gameOverInfo;

    public GameDelta(MoveCommand moveCommand, TurnInfo turnInfo, Map<Position, Integer> unitHealths, GameOverInfo gameOverInfo) {
        this.moveCommand = moveCommand;
        this.turnInfo = turnInfo;
        this.unitHealths = unitHealths;
        this.gameOverInfo = gameOverInfo;
    }

    public GameDelta(MoveCommand moveCommand, TurnInfo turnInfo, GameOverInfo gameOverInfo) {
        this.moveCommand = moveCommand;
        this.turnInfo = turnInfo;
        this.gameOverInfo = gameOverInfo;
    }

    public GameDelta(TurnInfo turnInfo, GameOverInfo gameOverInfo) {
        this.turnInfo = turnInfo;
        this.gameOverInfo = gameOverInfo;
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
}
