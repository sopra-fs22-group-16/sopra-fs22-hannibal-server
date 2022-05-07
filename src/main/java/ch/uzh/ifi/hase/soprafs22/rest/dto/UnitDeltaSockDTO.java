package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class UnitDeltaSockDTO {
    private MovementDTO movement;
    private HealthDTO health;

    public MovementDTO getMovement() {
        return movement;
    }

    public void setMovement(MovementDTO movement) {
        this.movement = movement;
    }

    public HealthDTO getHealth() {
        return health;
    }

    public void setHealth(HealthDTO health) {
        this.health = health;
    }
}
