package ch.uzh.ifi.hase.soprafs22.game.units;

import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;
import ch.uzh.ifi.hase.soprafs22.game.units.interfaces.IUnitBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnitDirector {
    private final IUnitBuilder unitBuilder;

    public UnitDirector(IUnitBuilder unitBuilder) {
        this.unitBuilder = unitBuilder;
    }

    public void make(Map<String, Object> unitStream, int row, int column) {
        this.unitBuilder.setType((String) unitStream.get("type"));
        this.unitBuilder.setHealth((int) unitStream.get("health"));
        this.unitBuilder.setDefenseList((List<Integer>)unitStream.get("defense"));
        this.unitBuilder.setAttackDamageList((List<Integer>)unitStream.get("attackDamage"));
        this.unitBuilder.setAttackRange((int) unitStream.get("attackRange"));
        this.unitBuilder.setMovementRange((int) unitStream.get("movementRange"));
        this.unitBuilder.setCommandList((List<String>)unitStream.get("commands"));
        this.unitBuilder.setTeamId((int)unitStream.get("teamId"));
        this.unitBuilder.setUserId((int)unitStream.get("userId"));
        this.unitBuilder.setPosition(row, column);
    }
}
