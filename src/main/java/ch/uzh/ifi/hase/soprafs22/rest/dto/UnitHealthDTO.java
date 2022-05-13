package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class UnitHealthDTO {
    private PositionDTO unitPosition;
    private int health;

    public UnitHealthDTO(PositionDTO unitPosition, int health) {
        this.unitPosition = unitPosition;
        this.health = health;
    }

    public PositionDTO getUnitPosition() {
        return unitPosition;
    }

    public int getHealth() {
        return health;
    }
}
