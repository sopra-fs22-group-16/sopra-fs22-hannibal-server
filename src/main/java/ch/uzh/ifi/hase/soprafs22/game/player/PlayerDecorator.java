package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.List;

public class PlayerDecorator extends BasePlayerDecorator {

    private final List<Unit> units;

    public PlayerDecorator(IPlayer player, List<Unit> unitList) {
        super(player);
        this.units = unitList;
    }

    public List<Unit> getUnits() {
        return units;
    }
}
