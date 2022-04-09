package ch.uzh.ifi.hase.soprafs22.exceptions;

public class PlayerNotFoundException extends Exception{
    private final String token;

    public PlayerNotFoundException(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }
}
