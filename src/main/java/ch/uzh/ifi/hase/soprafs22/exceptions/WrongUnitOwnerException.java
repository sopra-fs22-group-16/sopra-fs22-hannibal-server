package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

public class WrongUnitOwnerException extends Exception {
    private final Unit unit;
    private final long playerId;

    public WrongUnitOwnerException(Unit unit, long playerId) {
        this.unit = unit;
        this.playerId = playerId;
    }

    public Unit getUnit() {
        return unit;
    }

    public long getPlayerId() {
        return playerId;
    }
}
