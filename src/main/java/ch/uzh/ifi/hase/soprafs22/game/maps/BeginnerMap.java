package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.Tile;
import ch.uzh.ifi.hase.soprafs22.game.maps.interfaces.IMap;

import java.util.ArrayList;
import java.util.List;

public class BeginnerMap implements IMap {
    private List<List<Tile>> tiles;

    public BeginnerMap() {
        this.tiles = new ArrayList<>();
    }

    public void addRow(){
        tiles.add(new ArrayList<>());
    }

    public void addTile(int row, Tile tile){
        tiles.get(row).add(tile);
    }

    /*private final int length;
    private final int width;

    public BeginnerMap(int length, int width) {
        this.length = length;
        this.width = width;
    }*/

    @Override
    public Tile getTile(Position position) {
        return null;
    }
}
