package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

public class Player {
    private final long playerId;

    private String username;

    private final String token;

    private boolean ready;

    private RegisteredUser registeredUser;

    public Player(long id, String username, String token) {
        this.playerId = id;
        this.username = username;
        this.token = token;
        this.ready = false;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(registeredUser == null)
            this.username = username;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public void linkRegisteredUser(RegisteredUser registeredUser){
        this.registeredUser = registeredUser;
        this.username = registeredUser.getUsername();
    }

}
