package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;

import java.util.Map;

public class GameDelta {
    private final MoveCommand moveCommand;
    private final Map<Position, Integer> unitHealths;
    private final TurnInfo turnInfo;

    public GameDelta(MoveCommand moveCommand, TurnInfo turnInfo) {
        this.moveCommand = moveCommand;
        this.turnInfo = turnInfo;
        this.unitHealths = null;
    }

    public GameDelta(AttackCommand attackCommand, TurnInfo turnInfo, Map<Position, Integer> unitHealths) {
        this.turnInfo = turnInfo;
        this.unitHealths = unitHealths;
        this.moveCommand =  new MoveCommand();
        this.moveCommand.setStart(attackCommand.getAttacker());
        this.moveCommand.setDestination(attackCommand.getAttackerDestination());
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
