package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;

public class GameDelta {
    private MoveCommand moveCommand;
    private Map<Position, Integer> unitHealths;
    private TurnInfo turnInfo;

    public GameDelta(MoveCommand moveCommand, TurnInfo turnInfo, Map<Position, Integer> unitHealths) {
        this.moveCommand = moveCommand;
        this.turnInfo = turnInfo;
        this.unitHealths = unitHealths;
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
}
