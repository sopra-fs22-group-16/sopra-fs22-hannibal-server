package ch.uzh.ifi.hase.soprafs22.game.maps.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.Tile;

public interface IMap {
    Tile getTile(Position position);
}
