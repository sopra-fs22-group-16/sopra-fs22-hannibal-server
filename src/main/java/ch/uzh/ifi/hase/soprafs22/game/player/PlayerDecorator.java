package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.unit.IUnit;

import java.util.HashMap;
import java.util.Map;

public class PlayerDecorator extends BasePlayerDecorator {

    private final Map<Long, IUnit> units;

    public PlayerDecorator(IPlayer player) {
        super(player);
        this.units = new HashMap<>();
    }

    public IUnit getUnitById(long id){
        return units.get(id);
    }
}
