package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitCommandPutDTO;

public class UnitDeltaPutDTO {
    private UnitCommandPutDTO movement;
    private HealthPutDTO health;

    public UnitCommandPutDTO getMovement() {
        return movement;
    }

    public void setMovement(UnitCommandPutDTO movement) {
        this.movement = movement;
    }

    public HealthPutDTO getHealth() {
        return health;
    }

    public void setHealth(HealthPutDTO health) {
        this.health = health;
    }
}
