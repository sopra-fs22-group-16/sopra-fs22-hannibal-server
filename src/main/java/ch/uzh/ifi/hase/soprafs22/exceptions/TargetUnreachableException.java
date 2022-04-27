package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

public class TargetUnreachableException extends Exception {
    private final Unit unit;
    private final Position start;
    private final Position end;

    public TargetUnreachableException(Unit unit, Position start, Position end){
        this.unit = unit;
        this.start = start;
        this.end = end;
    }

    public Unit getUnit() {
        return unit;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }
}
