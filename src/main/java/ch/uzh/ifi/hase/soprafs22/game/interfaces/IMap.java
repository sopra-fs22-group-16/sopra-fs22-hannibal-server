package ch.uzh.ifi.hase.soprafs22.game.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.enums.Underground;

public interface IMap {
    int getWidth();
    int getLength();
    Underground getUnderground();
    int getSlope();
}
