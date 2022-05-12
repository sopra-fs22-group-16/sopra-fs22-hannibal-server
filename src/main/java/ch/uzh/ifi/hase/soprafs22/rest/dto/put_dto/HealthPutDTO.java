package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class HealthPutDTO {
    private PositionDTO unitPosition;
    private int health;

    public PositionDTO getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(PositionDTO unitPosition) {
        this.unitPosition = unitPosition;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
