package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.player.interfaces.IObserver;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.List;

public class PlayerDecorator extends BasePlayerDecorator implements IObserver {

    private final List<Unit> units;

    public PlayerDecorator(IPlayer player, List<Unit> unitList) {
        super(player);
        this.units = unitList;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public boolean resetUnitsMovedStatus(){
        this.units.forEach(u -> u.setMoved(false));
        return true;
    }

    @Override
    public void update(Unit unit) {
        if (unit.getHealth() <= 0) {
            units.remove(unit);
        }
    }
    public void surrender() {
        units.clear();
    }
}
