package ch.uzh.ifi.hase.soprafs22.game.units.commands;

import ch.uzh.ifi.hase.soprafs22.game.Position;

public class MoveCommand {
    Position start;
    Position destination;

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }
}
