package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.Position;

public class TileOutOfRangeException extends Exception {

    private final Position position;
    private final int xRange;
    private final int yRange;

    public TileOutOfRangeException(Position position, int xRange, int yRange) {
        this.position = position;
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Position (" + position.getX() + "," + position.getY() + ") is out of range of ("+xRange+", "+yRange+").";
    }
}
