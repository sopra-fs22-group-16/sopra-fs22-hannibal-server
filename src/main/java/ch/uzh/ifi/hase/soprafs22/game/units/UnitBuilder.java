package ch.uzh.ifi.hase.soprafs22.game.units;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;
import ch.uzh.ifi.hase.soprafs22.game.units.interfaces.IUnitBuilder;

import java.util.ArrayList;
import java.util.List;

public class UnitBuilder implements IUnitBuilder {
    private final Unit result;

    public UnitBuilder() {
        this.result = new Unit();
    }

    @Override
    public void setType(String type) {
        switch (type) {
            case "archer" -> this.result.setType(UnitType.ARCHER);
            case "knight" -> this.result.setType(UnitType.KNIGHT);
            case "war_elephant" -> this.result.setType(UnitType.WAR_ELEPHANT);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void setHealth(int health) {
        this.result.setHealth(health);
    }

    @Override
    public void setDefenseList(List<Integer> defense) {
        this.result.setDefenseList(defense);
    }

    @Override
    public void setAttackDamageList(List<Integer> attackDamage) {
        this.result.setAttackDamageList(attackDamage);
    }

    @Override
    public void setAttackRange(int attackRange) {
        this.result.setAttackRange(attackRange);
    }

    @Override
    public void setMovementRange(int movementRange) {
        this.result.setMovementRange(movementRange);
    }

    @Override
    public void setCommandList(List<String> commands) {
        List<UnitCommands> unitCommandList = new ArrayList<>();
        for (String c : commands) {
            switch (c) {
                case "move" -> unitCommandList.add(UnitCommands.MOVE);
                case "attack" -> unitCommandList.add(UnitCommands.ATTACK);
                case "wait" -> unitCommandList.add(UnitCommands.WAIT);
                default -> throw new IllegalArgumentException();
            }
        }
        this.result.setCommandList(unitCommandList);
    }

    @Override
    public void setTeamId(int teamId) {
        this.result.setTeamId(teamId);
    }

    @Override
    public void setUserId(int userId) {
        this.result.setUserId(userId);
    }

    @Override
    public void setPosition(int row, int column) {
        this.result.setPosition(new Position(row, column));
    }

    public Unit getResult() {
        return this.result;
    }
}
