package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class HealthDTO {
    private PositionDTO defenderPosition;
    private long health;

    public PositionDTO getDefenderPosition() {
        return defenderPosition;
    }

    public void setDefenderPosition(PositionDTO defenderPosition) {
        this.defenderPosition = defenderPosition;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }
}
