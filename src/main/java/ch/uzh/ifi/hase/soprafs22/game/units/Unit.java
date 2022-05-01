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
    private List<Integer> defenseList;
    private List<Integer> attackDamageList;
    private int attackRange;
    private int movementRange;
    private List<UnitCommands> commandList;
    private int teamId;
    private long userId;
    private Position position;

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

    public List<Integer> getDefenseList() {
        return defenseList;
    }

    public void setDefenseList(List<Integer> defense) {
        this.defenseList = defense;
    }

    public List<Integer> getAttackDamageList() {
        return attackDamageList;
    }

    public void setAttackDamageList(List<Integer> attackDamage) {
        this.attackDamageList = attackDamage;
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

    public List<UnitCommands> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<UnitCommands> commands) {
        this.commandList = commands;
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
        victim.health -= this.attackDamageList.get(victim.type.ordinal()) / victim.defenseList.get(this.type.ordinal());
        //counterattack
        this.health -= 1 / 3 * victim.attackDamageList.get(this.type.ordinal()) / this.defenseList.get(victim.type.ordinal());
        victim.notifyObservers(victim);
        this.notifyObservers(this);
    }
}
