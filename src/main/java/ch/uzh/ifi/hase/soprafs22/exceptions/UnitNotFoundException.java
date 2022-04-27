package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.Position;

public class UnitNotFoundException extends Exception {
    private final Position position;

    public UnitNotFoundException(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
