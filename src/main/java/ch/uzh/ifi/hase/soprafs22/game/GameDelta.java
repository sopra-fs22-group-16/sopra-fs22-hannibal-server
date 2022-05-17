package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;

public class GameDelta {
    private final MoveCommand moveCommand;
    private final Map<Position, Integer> unitHealths;
    private final TurnInfo turnInfo;

    private final GameOverInfo gameOverInfo;

    private final SurrenderInfo surrenderInfo;

    public GameDelta(MoveCommand moveCommand, TurnInfo turnInfo, Map<Position, Integer> unitHealths, GameOverInfo gameOverInfo, SurrenderInfo surrenderInfo) {
        this.turnInfo = turnInfo;
        this.moveCommand =  moveCommand;
        this.unitHealths = unitHealths;
        this.gameOverInfo = gameOverInfo;
        this.surrenderInfo = surrenderInfo;
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
