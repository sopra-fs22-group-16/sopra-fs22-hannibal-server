package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces.ITileBuilder;

import java.util.Map;

public class TileDirector {
    private final ITileBuilder tileBuilder;

    public TileDirector(ITileBuilder tileBuilder) {
        this.tileBuilder = tileBuilder;
    }

    public void make(Map<String, Object> tile) {
        tileBuilder.setType((String) tile.get("type"));
        tileBuilder.setVariant((String) tile.get("variant"));
        tileBuilder.setTraversability((Boolean) tile.get("traversable"));
        tileBuilder.setTraversingCost((Integer) tile.get("traversingCost"));
    }
}
