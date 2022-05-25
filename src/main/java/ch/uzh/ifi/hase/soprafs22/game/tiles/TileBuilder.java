package ch.uzh.ifi.hase.soprafs22.game.tiles;

import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.Type;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.Variant;
import ch.uzh.ifi.hase.soprafs22.game.tiles.interfaces.ITileBuilder;
import org.jetbrains.annotations.NotNull;

public class TileBuilder implements ITileBuilder {
    private final Tile result;

    public TileBuilder() {
        this.result = new Tile();
    }

    @Override
    public void setType(@NotNull String tileType) {
        switch (tileType) {
            case "border" -> this.result.setType(Type.BORDER);
            case "grass" -> this.result.setType(Type.GRASS);
            case "river" -> this.result.setType(Type.RIVER);
            case "bridge" -> this.result.setType(Type.BRIDGE);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void setVariant(@NotNull String tileVariant) {
        switch (tileVariant) {
            case "flat" -> this.result.setVariant(Variant.FLAT);
            case "top" -> this.result.setVariant(Variant.TOP);
            case "top_left" -> this.result.setVariant(Variant.TOP_LEFT);
            case "top_right" -> this.result.setVariant(Variant.TOP_RIGHT);
            case "border_left" -> this.result.setVariant(Variant.BORDER_LEFT);
            case "border_right" -> this.result.setVariant(Variant.BORDER_RIGHT);
            case "grassy" -> this.result.setVariant(Variant.GRASSY);
            case "right" -> this.result.setVariant(Variant.RIGHT);
            case "leafy_corner_top_left" -> this.result.setVariant(Variant.LEAFY_CORNER_TOP_LEFT);
            case "leafy_edge_top" -> this.result.setVariant(Variant.LEAFY_EDGE_TOP);
            case "leafy_edge_bottom" -> this.result.setVariant(Variant.LEAFY_EDGE_BOTTOM);
            case "leafy_corner_top_right" -> this.result.setVariant(Variant.LEAFY_CORNER_TOP_RIGHT);
            case "leafy_corner_bottom_left" -> this.result.setVariant(Variant.LEAFY_CORNER_BOTTOM_LEFT);
            case "leafy_edge_diagonal_bottom_left" -> this.result.setVariant(Variant.LEAFY_EDGE_DIAGONAL_BOTTOM_LEFT);
            case "leafy_corner_bottom_right" -> this.result.setVariant(Variant.LEAFY_CORNER_BOTTOM_RIGHT);
            case "leafy" -> this.result.setVariant(Variant.LEAFY);
            case "leafy_edge_right" -> this.result.setVariant(Variant.LEAFY_EDGE_RIGHT);
            case "leafy_edge_left" -> this.result.setVariant(Variant.LEAFY_EDGE_LEFT);
            case "leafy_edge_diagonal_top_right" -> this.result.setVariant(Variant.LEAFY_EDGE_DIAGONAL_TOP_RIGHT);
            case "leafy_edge_diagonal_bottom_right" -> this.result.setVariant(Variant.LEAFY_EDGE_DIAGONAL_BOTTOM_RIGHT);
            case "leafy_edge_diagonal_top_left" -> this.result.setVariant(Variant.LEAFY_EDGE_DIAGONAL_TOP_LEFT);
            case "left" -> this.result.setVariant(Variant.LEFT);
            case "corner_inverted_top_right" -> this.result.setVariant(Variant.CORNER_INVERTED_TOP_RIGHT);
            case "corner_top_right" -> this.result.setVariant(Variant.CORNER_TOP_RIGHT);
            case "corner_bottom_right" -> this.result.setVariant(Variant.CORNER_BOTTOM_RIGHT);
            case "corner_bottom_left" -> this.result.setVariant(Variant.CORNER_BOTTOM_LEFT);
            case "bottom" -> this.result.setVariant(Variant.BOTTOM);
            case "bottom_left" -> this.result.setVariant(Variant.BOTTOM_LEFT);
            case "bottom_right" -> this.result.setVariant(Variant.BOTTOM_RIGHT);
            case "corner_inverted_bottom_left" -> this.result.setVariant(Variant.CORNER_INVERTED_BOTTOM_LEFT);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void setTraversability(Boolean traversable) {
        this.result.setTraversable(traversable);
    }

    @Override
    public void setTraversingCost(int traversingCost) {
        this.result.setTraversingCost(traversingCost);
    }

    public Tile getResult() {
        return result;
    }
}
