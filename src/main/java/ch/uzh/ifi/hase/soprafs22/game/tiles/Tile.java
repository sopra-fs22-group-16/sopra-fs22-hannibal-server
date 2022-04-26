package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.Type;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.Variant;

public class Tile {
    private Type type;
    private Variant variant;
    private Boolean traversable;
    private int traversingCost;

    public Tile() {
    }

    public Type getType() {
        return type;
    }

    public Variant getVariant() {
        return variant;
    }

    public Boolean getTraversable() {
        return traversable;
    }

    public int getTraversingCost() {
        return traversingCost;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public void setTraversable(Boolean traversable) {
        this.traversable = traversable;
    }

    public void setTraversingCost(int traversingCost) {
        this.traversingCost = traversingCost;
    }
}
