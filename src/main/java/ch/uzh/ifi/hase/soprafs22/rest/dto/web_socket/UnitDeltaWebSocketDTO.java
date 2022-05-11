package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitMovePutDTO;

public class UnitDeltaWebSocketDTO {
    private UnitMovePutDTO movement;
    private HealthPutDTO health;

    public UnitMovePutDTO getMovement() {
        return movement;
    }

    public void setMovement(UnitMovePutDTO movement) {
        this.movement = movement;
    }

    public HealthPutDTO getHealth() {
        return health;
    }

    public void setHealth(HealthPutDTO health) {
        this.health = health;
    }
}
