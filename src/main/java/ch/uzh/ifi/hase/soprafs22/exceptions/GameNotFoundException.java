package ch.uzh.ifi.hase.soprafs22.exceptions;

public class GameNotFoundException extends Exception {

    private final long id;

    public GameNotFoundException(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
