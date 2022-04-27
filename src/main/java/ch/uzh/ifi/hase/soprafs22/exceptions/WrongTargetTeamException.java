package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

public class WrongTargetTeamException extends Exception {
    private final Unit first;
    private final Unit second;

    public WrongTargetTeamException(Unit first, Unit second) {
        this.first = first;
        this.second = second;
    }

    public Unit getFirst() {
        return first;
    }

    public Unit getSecond() {
        return second;
    }
}
