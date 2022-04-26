package ch.uzh.ifi.hase.soprafs22.game.units.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;

import java.util.List;

public interface IUnitBuilder {
    void setType(String type);

    void setHealth(int health);

    void setDefenseList(List<Integer> defense);

    void setAttackDamageList(List<Integer> attackDamage);

    void setAttackRange(int attackRange);

    void setMovementRange(int movementRange);

    void setCommandList(List<String> commands);

    void setTeamId(int teamId);

    void setUserId(int userId);

    void setPosition(int x, int y);
}
