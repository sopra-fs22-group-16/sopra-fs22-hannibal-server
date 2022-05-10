package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

public class HealthPutDTO {
    private PositionPutDTO defenderPosition;
    private long health;

    public PositionPutDTO getDefenderPosition() {
        return defenderPosition;
    }

    public void setDefenderPosition(PositionPutDTO defenderPosition) {
        this.defenderPosition = defenderPosition;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }
}
