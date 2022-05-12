package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class UnitMoveWebSocketDTO {
    private PositionDTO start;
    private PositionDTO destination;
    private boolean moved;

    public PositionDTO getStart() {
        return start;
    }

    public void setStart(PositionDTO start) {
        this.start = start;
    }

    public PositionDTO getDestination() {
        return destination;
    }

    public void setDestination(PositionDTO destination) {
        this.destination = destination;
    }

    public boolean getMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
