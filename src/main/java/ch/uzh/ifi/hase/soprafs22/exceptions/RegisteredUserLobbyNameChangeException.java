package ch.uzh.ifi.hase.soprafs22.exceptions;

public class RegisteredUserLobbyNameChangeException extends Exception{
    public final long idRegisteredPlayer;
    public RegisteredUserLobbyNameChangeException(long idRegisteredPlayer){
        this.idRegisteredPlayer = idRegisteredPlayer;
    }
    public long getIdRegisteredPlayer() {
        return idRegisteredPlayer;
    }
}
