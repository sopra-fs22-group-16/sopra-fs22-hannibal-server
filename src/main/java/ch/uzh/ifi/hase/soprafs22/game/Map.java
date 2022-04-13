package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.interfaces.IMap;

public class Map implements IMap {
    private int length;
    private int width;

    public Map(int length, int width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public Tile getTile(Position position) {
        return null;
    }
}
