package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class PlayerDecorator extends BasePlayerDecorator implements Observer {

    private final List<Unit> units;

    public PlayerDecorator(IPlayer player, List<Unit> unitList) {
        super(player);
        this.units = unitList;
    }

    public List<Unit> getUnits() {
        return units;
    }

    /**
     * Find a unit in the given position.
     *
     * @param position position in which to look for the unit.
     * @return Optional that either contains the unit or is empty.
     */
    public Optional<Unit> getUnitAt(Position position) {
        return units.stream().filter(u -> u.getPosition().equals(position)).findAny();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Unit && ((Unit) o).getHealth() <= 0) {
            units.remove(o);
        }
    }
}
