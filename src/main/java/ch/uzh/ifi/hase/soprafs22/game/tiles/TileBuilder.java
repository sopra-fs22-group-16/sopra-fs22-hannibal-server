package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileType;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileVariant;
import ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces.ITileBuilder;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitCommands;
import ch.uzh.ifi.hase.soprafs22.game.units.enums.UnitType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TileBuilder implements ITileBuilder {
    private final Tile tile;

    public TileBuilder() {
        this.tile = new Tile();
    }

    @Override
    public void setTileType(String tileType) {
        switch (tileType) {
            case "border" -> this.tile.setTileType(TileType.BORDER);
            case "grass" -> this.tile.setTileType(TileType.GRASS);
            case "river" -> this.tile.setTileType(TileType.RIVER);
            case "bridge" -> this.tile.setTileType(TileType.BRIDGE);
        }
    }

    @Override
    public void setTileVariant(String tileVariant) {
        switch (tileVariant) {
            case "flat" -> this.tile.setTileVariant(TileVariant.FLAT);
            case "top" -> this.tile.setTileVariant(TileVariant.TOP);
            case "border_left" -> this.tile.setTileVariant(TileVariant.BORDER_LEFT);
            case "border_right" -> this.tile.setTileVariant(TileVariant.BORDER_RIGHT);
            case "grassy" -> this.tile.setTileVariant(TileVariant.GRASSY);
            case "right" -> this.tile.setTileVariant(TileVariant.RIGHT);
            case "leafy_corner_top_left" -> this.tile.setTileVariant(TileVariant.LEAFY_CORNER_TOP_LEFT);
            case "leafy_edge_top" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_TOP);
            case "leafy_edge_bottom" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_BOTTOM);
            case "leafy_corner_top_right" -> this.tile.setTileVariant(TileVariant.LEAFY_CORNER_TOP_RIGHT);
            case "leafy_corner_bottom_left" -> this.tile.setTileVariant(TileVariant.LEAFY_CORNER_BOTTOM_LEFT);
            case "leafy_edge_diagonal_bottom_left" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_BOTTOM_LEFT);
            case "leafy_corner_bottom_right" -> this.tile.setTileVariant(TileVariant.LEAFY_CORNER_BOTTOM_RIGHT);
            case "leafy" -> this.tile.setTileVariant(TileVariant.LEAFY);
            case "leafy_edge_right" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_RIGHT);
            case "leafy_edge_left" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_LEFT);
            case "leafy_edge_diagonal_top_right" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_TOP_RIGHT);
            case "leafy_edge_diagonal_bottom_right" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_BOTTOM_RIGHT);
            case "leafy_edge_diagonal_top_left" -> this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_TOP_LEFT);
            case "left" -> this.tile.setTileVariant(TileVariant.LEFT);
            case "corner_inverted_top_right" -> this.tile.setTileVariant(TileVariant.CORNER_INVERTED_TOP_RIGHT);
            case "corner_top_right" -> this.tile.setTileVariant(TileVariant.CORNER_TOP_RIGHT);
            case "corner_bottom_right" -> this.tile.setTileVariant(TileVariant.CORNER_BOTTOM_RIGHT);
            case "bottom" -> this.tile.setTileVariant(TileVariant.BOTTOM);
            case "corner_inverted_bottom_left" -> this.tile.setTileVariant(TileVariant.CORNER_INVERTED_BOTTOM_LEFT);
        }
    }

    @Override
    public void setTileTraversability(Boolean traversable) {
        this.tile.setTraversable(traversable);
    }

    @Override
    public void setTileTraversingCost(int traversingCost) {
        this.tile.setTraversingCost(traversingCost);
    }

    //TODO create a UnitBuilder analogous to the TileBuilder
    @Override
    public void setTileUnit(LinkedHashMap<String, Object> unitStream) {
        String unitType = (String) unitStream.get("type");
        Unit unit = new Unit();
        switch (unitType) {
            case "archer" -> unit.setType(UnitType.ARCHER);
            case "knight" -> unit.setType(UnitType.KNIGHT);
            case "war_elephant" -> unit.setType(UnitType.WAR_ELEPHANT);
        }
        unit.setHealth((int) unitStream.get("health"));
        unit.setDefense((List<Integer>)unitStream.get("defense"));
        unit.setAttackDamage((List<Integer>)unitStream.get("attackDamage"));
        unit.setAttackRange((int) unitStream.get("attackRange"));
        unit.setMovementRange((int) unitStream.get("movementRange"));
        List<String> commandList = (List<String>)unitStream.get("commands");
        List<UnitCommands> unitCommandList = new ArrayList<>();
        for(String c : commandList){
            switch (c) {
                case "move" -> unitCommandList.add(UnitCommands.MOVE);
                case "attack" -> unitCommandList.add(UnitCommands.ATTACK);
                case "wait" -> unitCommandList.add(UnitCommands.WAIT);
            }
        }
        unit.setCommands(unitCommandList);
        unit.setTeamId((int)unitStream.get("teamId"));
        unit.setUserId((int)unitStream.get("userId"));
        this.tile.setUnit(unit);
    }

    public Tile getResult() {
        return this.tile;
    }
}
