package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.exceptions.TileOutOfRangeException;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;

import java.util.List;

public class GameMap {
    private final List<List<Tile>> tiles;

    public GameMap(List<List<Tile>> tiles) {
        this.tiles = tiles;
    }

    // Only used for testing.
    public List<List<Tile>> getTiles() {
        return tiles;
    }
}
