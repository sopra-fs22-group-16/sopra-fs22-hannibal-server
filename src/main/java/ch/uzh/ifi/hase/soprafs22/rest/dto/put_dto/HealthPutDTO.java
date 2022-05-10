package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

public class HealthPutDTO {
    private PositionPutDTO unitPosition;
    private int health;

    public PositionPutDTO getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(PositionPutDTO unitPosition) {
        this.unitPosition = unitPosition;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
