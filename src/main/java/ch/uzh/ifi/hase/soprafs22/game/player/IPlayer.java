package ch.uzh.ifi.hase.soprafs22.game.player;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;

public interface IPlayer {

    long getId();

    String getToken();

    String getName();

    void setName(String name);

    boolean isReady();

    void setReady(boolean isReady);

    Team getTeam();

    void setTeam(Team team);

    RegisteredUser getRegisteredUser();

    void linkRegisteredUser(RegisteredUser registeredUser);

}
