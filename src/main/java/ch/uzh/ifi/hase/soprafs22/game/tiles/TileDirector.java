package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces.ITileBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class TileDirector {
    private final ITileBuilder tileBuilder;

    public TileDirector(ITileBuilder tileBuilder) {
        this.tileBuilder = tileBuilder;
    }

    public void make(LinkedHashMap<String, Object> tile) {
        tileBuilder.setTileType((String) tile.get("type"));
        tileBuilder.setTileVariant((String) tile.get("variant"));
        tileBuilder.setTileTraversability((Boolean) tile.get("traversable"));
        tileBuilder.setTileTraversingCost((Integer) tile.get("traversingCost"));
        if(tile.get("unit")!=null){
            tileBuilder.setTileUnit((Map<String, Object>) tile.get("unit"));
        }
    }
}
