package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.PositionPutDTO;

public class UnitCommandPutDTO {
    private PositionPutDTO start;
    private PositionPutDTO end;

    public PositionPutDTO getStart() {
        return start;
    }

    public void setStart(PositionPutDTO start) {
        this.start = start;
    }

    public PositionPutDTO getEnd() {
        return end;
    }

    public void setEnd(PositionPutDTO end) {
        this.end = end;
    }
}
