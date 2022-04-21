package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Type;
import ch.uzh.ifi.hase.soprafs22.game.enums.Variant;

public class Tile {
    private Type type;
    private Variant variant;
    private Boolean traversable;
    private int traversingCost;

    public Tile() {
    }

    public Tile(Type type, Variant variant, Boolean traversable, int traversingCost) {
        this.type = type;
        this.variant = variant;
        this.traversable = traversable;
        this.traversingCost = traversingCost;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
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
