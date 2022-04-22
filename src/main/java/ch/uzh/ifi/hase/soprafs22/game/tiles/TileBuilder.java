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
        if (tileType.equals("border")) {
            this.tile.setTileType(TileType.BORDER);
        }
        else if (tileType.equals("grass")) {
            this.tile.setTileType(TileType.GRASS);
        }
        else if (tileType.equals("river")) {
            this.tile.setTileType(TileType.RIVER);
        }
        else if (tileType.equals("bridge")) {
            this.tile.setTileType(TileType.BRIDGE);
        }
    }

    @Override
    public void setTileVariant(String tileVariant) {
        if (tileVariant.equals("flat")) {
            this.tile.setTileVariant(TileVariant.FLAT);
        }
        else if (tileVariant.equals("top")) {
            this.tile.setTileVariant(TileVariant.TOP);
        }
        else if (tileVariant.equals("border_left")) {
            this.tile.setTileVariant(TileVariant.BORDER_LEFT);
        }
        else if (tileVariant.equals("border_right")) {
            this.tile.setTileVariant(TileVariant.BORDER_RIGHT);
        }
        else if (tileVariant.equals("grassy")) {
            this.tile.setTileVariant(TileVariant.GRASSY);
        }
        else if (tileVariant.equals("right")) {
            this.tile.setTileVariant(TileVariant.RIGHT);
        }
        else if (tileVariant.equals("leafy_corner_top_left")) {
            this.tile.setTileVariant(TileVariant.LEAFY_CORNER_TOP_LEFT);
        }
        else if (tileVariant.equals("leafy_edge_top")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_TOP);
        }
        else if (tileVariant.equals("leafy_edge_bottom")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_BOTTOM);
        }
        else if (tileVariant.equals("leafy_corner_top_right")) {
            this.tile.setTileVariant(TileVariant.LEAFY_CORNER_TOP_RIGHT);
        }
        else if (tileVariant.equals("leafy_corner_bottom_left")) {
            this.tile.setTileVariant(TileVariant.LEAFY_CORNER_BOTTOM_LEFT);
        }
        else if (tileVariant.equals("leafy_edge_diagonal_bottom_left")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_BOTTOM_LEFT);
        }
        else if (tileVariant.equals("leafy_corner_bottom_right")) {
            this.tile.setTileVariant(TileVariant.LEAFY_CORNER_BOTTOM_RIGHT);
        }
        else if (tileVariant.equals("leafy")) {
            this.tile.setTileVariant(TileVariant.LEAFY);
        }
        else if (tileVariant.equals("leafy_edge_right")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_RIGHT);
        }
        else if (tileVariant.equals("leafy_edge_left")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_LEFT);
        }
        else if (tileVariant.equals("leafy_edge_diagonal_top_right")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_TOP_RIGHT);
        }
        else if (tileVariant.equals("leafy_edge_diagonal_bottom_right")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_BOTTOM_RIGHT);
        }
        else if (tileVariant.equals("leafy_edge_diagonal_top_left")) {
            this.tile.setTileVariant(TileVariant.LEAFY_EDGE_DIAGONAL_TOP_LEFT);
        }
        else if (tileVariant.equals("left")) {
            this.tile.setTileVariant(TileVariant.LEFT);
        }
        else if (tileVariant.equals("corner_inverted_top_right")) {
            this.tile.setTileVariant(TileVariant.CORNER_INVERTED_TOP_RIGHT);
        }
        else if (tileVariant.equals("corner_top_right")) {
            this.tile.setTileVariant(TileVariant.CORNER_TOP_RIGHT);
        }
        else if (tileVariant.equals("corner_bottom_right")) {
            this.tile.setTileVariant(TileVariant.CORNER_BOTTOM_RIGHT);
        }
        else if (tileVariant.equals("bottom")) {
            this.tile.setTileVariant(TileVariant.BOTTOM);
        }
        else if (tileVariant.equals("corner_inverted_bottom_left")) {
            this.tile.setTileVariant(TileVariant.CORNER_INVERTED_BOTTOM_LEFT);
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
        if(unitType.equals("archer")){
            unit.setType(UnitType.ARCHER);
        }
        else if(unitType.equals("knight")){
            unit.setType(UnitType.KNIGHT);
        }
        else if(unitType.equals("war_elephant")){
            unit.setType(UnitType.WAR_ELEPHANT);
        }
        unit.setHealth((int) unitStream.get("health"));
        unit.setDefense((List<Integer>)unitStream.get("defense"));
        unit.setAttackDamage((List<Integer>)unitStream.get("attackDamage"));
        unit.setAttackRange((int) unitStream.get("attackRange"));
        unit.setMovementRange((int) unitStream.get("movementRange"));
        List<String> commandList = (List<String>)unitStream.get("commands");
        List<UnitCommands> unitCommandList = new ArrayList<>();
        for(String c : commandList){
            if(c.equals("move")){
                unitCommandList.add(UnitCommands.MOVE);
            }
            else if(c.equals("attack")){
                unitCommandList.add(UnitCommands.ATTACK);
            }
            else if(c.equals("wait")){
                unitCommandList.add(UnitCommands.WAIT);
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
