package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.exceptions.TileOutOfRangeException;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;

import java.util.List;

public class GameMap {
    private List<List<Tile>> tiles;

    public GameMap(List<List<Tile>> tiles) {
        this.tiles = tiles;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }

    public void setTiles(List<List<Tile>> tiles) {
        this.tiles = tiles;
    }

    /**
     * Get a tile from the map.
     * @param position, the position from which to get the Tile.
     * @return
     */
    public Tile getTile(Position position) throws TileOutOfRangeException{
        List<Tile> columns = tiles.get(position.getX());
        if (columns == null)
            throw new TileOutOfRangeException(position);
        Tile tile = columns.get(position.getY());
        if (tile == null)
            throw new TileOutOfRangeException(position);
        return tile;
    }
}
