package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class HealthDTO {
    private PositionDTO unitPosition;
    private long health;

    public PositionDTO getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(PositionDTO unitPosition) {
        this.unitPosition = unitPosition;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }
}
