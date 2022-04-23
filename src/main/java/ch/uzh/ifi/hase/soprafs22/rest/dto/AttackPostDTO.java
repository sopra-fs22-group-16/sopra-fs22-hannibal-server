package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class AttackPostDTO {
  private PositionDTO attacker;
  private PositionDTO defender;

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
}
