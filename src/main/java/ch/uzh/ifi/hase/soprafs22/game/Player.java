package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

public class Player {
    private final long id;

    private String name;

    private final String token;

    private boolean ready;

    private Team team;

    private RegisteredUser registeredUser;

    public Player(long id, String name, String token, Team team) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.team = team;
        this.ready = false;
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        if(registeredUser != null) return registeredUser.getUsername();
        return name;
    }

    public void setName(String name) {
        if(registeredUser == null) this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    public void linkRegisteredUser(RegisteredUser registeredUser){
        this.registeredUser = registeredUser;
        this.name = registeredUser.getUsername();
    }

}
