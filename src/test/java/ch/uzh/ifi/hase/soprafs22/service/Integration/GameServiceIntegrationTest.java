package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.GameDelta;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Autowired
    GameService gameService;

    @Autowired
    private LobbyService lobbyService;

    @Qualifier("registeredUserRepository")
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Clear lobbyManager
        LobbyManager.getInstance().clear();
        userRepository.deleteAll();
    }

    @Test
    void endOfRankedGame_1v1_updateRegisteredUser_validInputs_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.RANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // create lobby

        ILobby createdLobby = lobbyService.createLobby(registeredUser.getToken(), lobbyName, visibility, gameMode, gameType);

        // Create a second registered user
        RegisteredUser registeredUser2 = new RegisteredUser();
        registeredUser2.setUsername("registeredUser2");
        registeredUser2.setPassword("password2");
        registeredUser2.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser2);

        lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), registeredUser2.getToken());

        lobbyService.modifyPlayer(registeredUser.getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(registeredUser2.getToken(), createdLobby.getId(), null, true);

        lobbyService.createGame(registeredUser.getToken(), createdLobby.getId());

        gameService.surrender(createdLobby.getId(), registeredUser.getToken());

        RegisteredUser player1 = userRepository.findRegisteredUserByToken(registeredUser.getToken());
        assertEquals(900, player1.getRankedScore());
        assertEquals(1, player1.getLosses());
        assertEquals(0, player1.getWins());

        RegisteredUser player2 = userRepository.findRegisteredUserByToken(registeredUser2.getToken());
        assertEquals(1100, player2.getRankedScore());
        assertEquals(0, player2.getLosses());
        assertEquals(1, player2.getWins());

    }

    @Test
    void endOfRankedGame_1v1_Draw_validInputs_success() {
        // given
        String lobbyName = "lobbyName";
        Visibility visibility = Visibility.PRIVATE;
        GameMode gameMode = GameMode.ONE_VS_ONE;
        GameType gameType = GameType.RANKED;

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("registeredUser");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser);

        // create lobby

        ILobby createdLobby = lobbyService.createLobby(registeredUser.getToken(), lobbyName, visibility, gameMode, gameType);

        // Create a second registered user
        RegisteredUser registeredUser2 = new RegisteredUser();
        registeredUser2.setUsername("registeredUser2");
        registeredUser2.setPassword("password2");
        registeredUser2.setToken(UUID.randomUUID().toString());

        userRepository.saveAndFlush(registeredUser2);

        lobbyService.addPlayer(createdLobby.getInvitationCode(), createdLobby.getId(), registeredUser2.getToken());

        lobbyService.modifyPlayer(registeredUser.getToken(), createdLobby.getId(), null, true);
        lobbyService.modifyPlayer(registeredUser2.getToken(), createdLobby.getId(), null, true);

        Game game = lobbyService.createGame(registeredUser.getToken(), createdLobby.getId());

        for(PlayerDecorator player: game.getDecoratedPlayers().values()){
            player.getUnits().clear();
        }

        GameDelta gameDelta = game.getCurrentGameDelta();

        assertNotNull(gameDelta.getGameOverInfo());
        assertTrue(gameDelta.getGameOverInfo().getWinners().isEmpty());

    }

}
