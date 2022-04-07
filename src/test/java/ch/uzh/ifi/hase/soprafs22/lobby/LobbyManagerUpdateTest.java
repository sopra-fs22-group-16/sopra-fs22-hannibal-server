package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LobbyManagerUpdateTest {

    private static LobbyManager lobbyManager;

    private ILobby LOBBY1;
    //private static final ILobby LOBBY2 = new Lobby("2L", LobbyMode.PUBLIC, USER2);

    @BeforeAll
    static void setup(){
        lobbyManager = LobbyManager.getInstance();
    }


    @BeforeEach
    public void before() throws SmallestIdNotCreatableException {
        //new Lobby("Lobby 1", LobbyMode.PRIVATE, /*host=*/ USER1);
        lobbyManager.clear();
        LOBBY1 = lobbyManager.createLobby("Lobby 1", Visibility.PUBLIC);
        //lobbyManager.addLobby(LOBBY2);
    }
}
