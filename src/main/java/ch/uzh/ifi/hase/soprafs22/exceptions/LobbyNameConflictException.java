package ch.uzh.ifi.hase.soprafs22.exceptions;

public class LobbyNameConflictException extends Exception{

    private final String conflictingName;

    public LobbyNameConflictException(String errorMessage, String conflictingName){
        super(errorMessage);
        this.conflictingName = conflictingName;
    }

    public String getConflictingName() {
        return conflictingName;
    }
}
