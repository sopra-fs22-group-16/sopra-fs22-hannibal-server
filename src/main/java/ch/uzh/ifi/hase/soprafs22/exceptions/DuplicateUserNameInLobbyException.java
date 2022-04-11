package ch.uzh.ifi.hase.soprafs22.exceptions;

public class DuplicateUserNameInLobbyException extends Exception {
    private final String userName;

    public DuplicateUserNameInLobbyException(String userName) {
        this.userName = userName;
    }

    public String userName() {
        return userName;
    }
}
