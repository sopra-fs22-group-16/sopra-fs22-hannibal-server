package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

public class UnitMovePutDTO {
    private PositionPutDTO start;
    private PositionPutDTO destination;

    public PositionPutDTO getStart() {
        return start;
    }

    public void setStart(PositionPutDTO start) {
        this.start = start;
    }

    public PositionPutDTO getDestination() {
        return destination;
    }

    public void setDestination(PositionPutDTO destination) {
        this.destination = destination;
    }
}
