package ch.uzh.ifi.hase.soprafs22.exceptions;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

public class AttackOutOfRangeException extends Exception {
    private final Unit attacker;
    private final Unit defender;
    public AttackOutOfRangeException(Unit attacker, Unit defender){
        this.attacker = attacker;
        this.defender = defender;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public Unit getDefender() {
        return defender;
    }
}
