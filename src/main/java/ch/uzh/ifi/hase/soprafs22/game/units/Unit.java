package ch.uzh.ifi.hase.soprafs22.game.units;

import ch.uzh.ifi.hase.soprafs22.exceptions.AttackOutOfRangeException;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;

import java.util.List;
import java.util.Observable;

public class Unit extends Observable {
    private UnitType type;
    private int health;
    private List<Double> defense;
    private List<Double> attackDamage;
    private int attackRange;
    private int movementRange;
    private List<UnitCommands> commands;
    private int teamId;
    private long userId;
    private Position position;
    private boolean moved;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public List<Double> getDefense() {
        return defense;
    }

    public void setDefense(List<Double> defense) {
        this.defense = defense;
    }

    public List<Double> getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(List<Double> attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public void setMovementRange(int movementRange) {
        this.movementRange = movementRange;
    }

    public List<UnitCommands> getCommands() {
        return commands;
    }

    public void setCommands(List<UnitCommands> commands) {
        this.commands = commands;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void attack(Unit victim) throws AttackOutOfRangeException {
        victim.health -= this.attackDamage.get(victim.type.ordinal()) / victim.defense.get(this.type.ordinal());
        //counterattack
        this.health -= 1 / 3 * victim.attackDamage.get(this.type.ordinal()) / this.defense.get(victim.type.ordinal());
        victim.notifyObservers(victim);
        this.notifyObservers(this);
    }

    public boolean getMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
