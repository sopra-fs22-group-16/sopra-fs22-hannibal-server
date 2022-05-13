package ch.uzh.ifi.hase.soprafs22.game.units.commands;

import ch.uzh.ifi.hase.soprafs22.game.Position;

public class AttackCommand {
    Position attacker;
    Position defender;
    Position attackerDestination;

    public AttackCommand(Position attacker, Position defender, Position attackerDestination) {
        this.attacker = attacker;
        this.defender = defender;
        this.attackerDestination = attackerDestination;
    }

    public Position getAttacker() {
        return attacker;
    }

    public Position getDefender() {
        return defender;
    }

    public Position getAttackerDestination() {
        return attackerDestination;
    }
}
