package ch.uzh.ifi.hase.soprafs22.game.units;

import ch.uzh.ifi.hase.soprafs22.exceptions.AttackOutOfRangeException;
import ch.uzh.ifi.hase.soprafs22.exceptions.TargetUnreachableException;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;

import java.util.List;

public class Unit {
    private UnitType type;
    private int health;
    private List<Integer> defense;
    private List<Integer> attackDamage;
    private int attackRange;
    private int movementRange;
    private List<UnitCommands> commands;
    private int teamId;
    private int userId;

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

    public List<Integer> getDefense() {
        return defense;
    }

    public void setDefense(List<Integer> defense) {
        this.defense = defense;
    }

    public List<Integer> getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(List<Integer> attackDamage) {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void attack(Unit unit) throws AttackOutOfRangeException {

    }

    public void move(Position from, Position to) throws TargetUnreachableException {

    }

    public void unitWait() {

    }
}
