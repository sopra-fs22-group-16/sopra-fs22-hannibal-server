package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlayerDecorator extends BasePlayerDecorator implements Observer {

    private final List<Unit> units;

    public PlayerDecorator(IPlayer player, List<Unit> unitList) {
        super(player);
        this.units = unitList;
    }

    public List<Unit> getUnits() {
        return units;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Unit && ((Unit) o).getHealth() <= 0) {
            units.remove(o);
        }
    }
}
