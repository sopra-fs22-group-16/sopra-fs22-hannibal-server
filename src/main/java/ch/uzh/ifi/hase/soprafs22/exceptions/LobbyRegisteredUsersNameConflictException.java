package ch.uzh.ifi.hase.soprafs22.exceptions;

public class LobbyRegisteredUsersNameConflictException extends LobbyNameConflictException{
    public LobbyRegisteredUsersNameConflictException(String errorMessage, String conflictingName){
        super(errorMessage, conflictingName);
    }
}
