package ch.uzh.ifi.hase.soprafs22.game.units.interfaces;

import ch.uzh.ifi.hase.soprafs22.game.player.interfaces.IObserver;

public interface IObservable {
        void registerObserver(IObserver observer);
        void removeObserver(IObserver observer);
        void notifyObservers();
}
