package ch.uzh.ifi.hase.soprafs22.game.units.commands;

import ch.uzh.ifi.hase.soprafs22.game.Position;

public class AttackCommand {
    Position attacker;
    Position defender;
    Position attackerDestination;

    public Position getAttacker() {
        return attacker;
    }

    public void setAttacker(Position attacker) {
        this.attacker = attacker;
    }

    public Position getDefender() {
        return defender;
    }

    public void setDefender(Position defender) {
        this.defender = defender;
    }

    public Position getAttackerDestination() {
        return attackerDestination;
    }

    public void setAttackerDestination(Position attackerDestination) {
        this.attackerDestination = attackerDestination;
    }
}
