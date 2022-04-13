package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.abstract_classes.AbstractMapFactory;
import ch.uzh.ifi.hase.soprafs22.game.interfaces.IMap;

public class MapFactory extends AbstractMapFactory {
    public IMap createMap() {
        return new Map(32, 32);
    }
}
