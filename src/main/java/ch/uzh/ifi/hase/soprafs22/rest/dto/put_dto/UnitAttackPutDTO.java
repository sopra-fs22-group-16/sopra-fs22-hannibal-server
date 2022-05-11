package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

public class UnitAttackPutDTO {
    private PositionPutDTO attacker;
    private PositionPutDTO defender;
    private PositionPutDTO attackerDestination;

    public PositionPutDTO getAttacker() {
        return attacker;
    }

    public void setAttacker(PositionPutDTO attacker) {
        this.attacker = attacker;
    }

    public PositionPutDTO getDefender() {
        return defender;
    }

    public void setDefender(PositionPutDTO defender) {
        this.defender = defender;
    }

    public PositionPutDTO getAttackerDestination() {
        return attackerDestination;
    }

    public void setAttackerDestination(PositionPutDTO attackerDestination) {
        this.attackerDestination = attackerDestination;
    }
}
