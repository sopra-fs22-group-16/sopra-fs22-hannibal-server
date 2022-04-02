package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    public void linkRegisteredUser(){

        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUsername");
        Player player = new Player(0L, "username", "token", Team.Red);

        // when
        player.linkRegisteredUser(registeredUser);

        // then
        assertEquals(registeredUser, player.getRegisteredUser());
        assertEquals(registeredUser.getUsername(), player.getUsername());
    }

    @Test
    public void setUsername_registeredUser_noChange(){
        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUsername");
        Player player = new Player(0L, "username", "token", Team.Red);
        player.linkRegisteredUser(registeredUser);

        // when
        player.setUsername("newUsername");

        // then no change
        assertEquals(registeredUser.getUsername(), player.getUsername());

    }
}