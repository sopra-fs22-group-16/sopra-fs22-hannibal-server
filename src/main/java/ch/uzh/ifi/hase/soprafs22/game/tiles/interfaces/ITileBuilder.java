package ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces;

public interface ITileBuilder {
    void setType(String tileType);
    void setVariant(String tileVariant);
    void setTraversability(Boolean traversable);
    void setTraversingCost(int traversingCost);
}
