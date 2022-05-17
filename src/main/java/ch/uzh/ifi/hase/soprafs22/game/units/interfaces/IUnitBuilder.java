package ch.uzh.ifi.hase.soprafs22.game.units.interfaces;

import java.util.List;

public interface IUnitBuilder {
    void setType(String type);

    void setHealth(int health);

    void setMaxHealth(int maxHealth);

    void setDefense(List<Double> defense);

    void setAttackDamage(List<Double> attackDamage);

    void setAttackRange(int attackRange);

    void setMovementRange(int movementRange);

    void setCommandList(List<String> commands);

    void setTeamId(int teamId);

    void setUserId(int userId);

    void setPosition(int x, int y);
}
