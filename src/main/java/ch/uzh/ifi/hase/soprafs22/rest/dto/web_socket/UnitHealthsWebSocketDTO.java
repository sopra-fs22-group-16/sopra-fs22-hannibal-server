package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class UnitHealthsWebSocketDTO {
    private PositionDTO unitPosition;
    private int health;

    public UnitHealthsWebSocketDTO(PositionDTO unitPosition, int health) {
        this.unitPosition = unitPosition;
        this.health = health;
    }

    public PositionDTO getUnitPosition() {
        return unitPosition;
    }

    public long getHealth() {
        return health;
    }
}
