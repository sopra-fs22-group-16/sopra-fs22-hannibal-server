package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

public abstract class BasePlayerDecorator implements IPlayer {

    private final IPlayer player;

    protected BasePlayerDecorator(IPlayer player){
     this.player = player;
    }

    @Override
    public long getId() {
        return player.getId();
    }

    @Override
    public String getToken() {
        return player.getToken();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void setName(String name) {
        player.setName(name);
    }

    @Override
    public boolean isReady() {
        return player.isReady();
    }

    @Override
    public void setReady(boolean isReady) {
        player.setReady(isReady);
    }

    @Override
    public Team getTeam() {
        return player.getTeam();
    }

    @Override
    public void setTeam(Team team) {
        player.setTeam(team);
    }

    @Override
    public RegisteredUser getRegisteredUser() {
        return player.getRegisteredUser();
    }

    @Override
    public void linkRegisteredUser(RegisteredUser registeredUser) {
        player.linkRegisteredUser(registeredUser);
    }
}
