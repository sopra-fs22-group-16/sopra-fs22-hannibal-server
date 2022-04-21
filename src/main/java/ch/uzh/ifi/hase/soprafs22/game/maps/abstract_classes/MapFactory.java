package ch.uzh.ifi.hase.soprafs22.game.maps.abstract_classes;

import ch.uzh.ifi.hase.soprafs22.game.maps.interfaces.IMap;

public abstract class MapFactory {
    protected abstract IMap createMap();
}
