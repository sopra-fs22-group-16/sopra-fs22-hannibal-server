package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.HashMap;
import java.util.Map;

public class PlayerDecorator extends BasePlayerDecorator {

    private final Map<Long, Unit> units;

    public PlayerDecorator(IPlayer player) {
        super(player);
        this.units = new HashMap<>();
    }

    public Unit getUnitById(long id){
        return units.get(id);
    }
}
