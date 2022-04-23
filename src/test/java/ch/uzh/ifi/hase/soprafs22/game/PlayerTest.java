package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void linkRegisteredUser(){

        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUsername");
        Player player = new Player(0L, "username", "token", Team.RED);

        // when
        player.linkRegisteredUser(registeredUser);

        // then
        assertEquals(registeredUser, player.getRegisteredUser());
        assertEquals(registeredUser.getUsername(), player.getName());
    }

    @Test
    void setUsername_registeredUser_noChange(){
        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUsername");
        Player player = new Player(0L, "username", "token", Team.RED);
        player.linkRegisteredUser(registeredUser);

        // when
        player.setName("newUsername");

        // then no change
        assertEquals(registeredUser.getUsername(), player.getName());

    }
}