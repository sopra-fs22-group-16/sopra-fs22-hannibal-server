package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class UnitAttackPutDTO {
    private PositionDTO attacker;
    private PositionDTO defender;
    private PositionDTO attackerDestination;

    public PositionDTO getAttacker() {
        return attacker;
    }

    public void setAttacker(PositionDTO attacker) {
        this.attacker = attacker;
    }

    public PositionDTO getDefender() {
        return defender;
    }

    public void setDefender(PositionDTO defender) {
        this.defender = defender;
    }

    public PositionDTO getAttackerDestination() {
        return attackerDestination;
    }

    public void setAttackerDestination(PositionDTO attackerDestination) {
        this.attackerDestination = attackerDestination;
    }
}
