package ch.uzh.ifi.hase.soprafs22.game.abstract_classes;

import ch.uzh.ifi.hase.soprafs22.game.interfaces.IMap;

public abstract class AbstractMapFactory {
    protected abstract IMap createMap();
}
