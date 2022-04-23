package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileType;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileVariant;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;

public class Tile {
    private TileType tileType;
    private TileVariant tileVariant;
    private Boolean traversable;
    private int traversingCost;
    private Unit unit;

    public Tile() {
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public TileVariant getTileVariant() {
        return tileVariant;
    }

    public void setTileVariant(TileVariant tileVariant) {
        this.tileVariant = tileVariant;
    }

    public Boolean getTraversable() {
        return traversable;
    }

    public void setTraversable(Boolean traversable) {
        this.traversable = traversable;
    }

    public int getTraversingCost() {
        return traversingCost;
    }

    public void setTraversingCost(int traversingCost) {
        this.traversingCost = traversingCost;
    }
}
