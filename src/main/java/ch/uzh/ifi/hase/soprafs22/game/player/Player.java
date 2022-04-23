package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

public class Player implements IPlayer {
    private final long id;

    private String name;

    private String token;

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

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getName() {
        if(registeredUser != null) return registeredUser.getUsername();
        return name;
    }

    @Override
    public void setName(String name) {
        if(registeredUser == null) this.name = name;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }

    @Override
    public void linkRegisteredUser(RegisteredUser registeredUser, String token){
        this.registeredUser = registeredUser;
        this.name = registeredUser.getUsername();
        this.token = token;
    }

}
