package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class MovementDTO {
    private PositionDTO start;
    private PositionDTO end;

    public PositionDTO getStart() {
        return start;
    }

    public void setStart(PositionDTO start) {
        this.start = start;
    }

    public PositionDTO getEnd() {
        return end;
    }

    public void setEnd(PositionDTO end) {
        this.end = end;
    }
}
