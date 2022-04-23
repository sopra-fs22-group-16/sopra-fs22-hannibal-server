package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerGetDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
class DTOMapperTest {

    private static final ILobby LOBBY = new Lobby(1L,"myLobbyName", Visibility.PRIVATE);
    @Test
    void testCreateLobby_fromLobby_toLobbyGetDTO_success() {
        // create Lobby

        // MAP -> LobbyGetDTO
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(LOBBY, LOBBY.getHost().getToken());

        // check content
        assertEquals(lobbyGetDTO.getId(), LOBBY.getId());
        assertEquals(lobbyGetDTO.getName(), LOBBY.getName());
        assertEquals(lobbyGetDTO.getHostId(), LOBBY.getHost().getId());

        int counter = 0;
        for (IPlayer player : LOBBY) {
            // Check that there exists an element in the lobbyGetDTO
            assertTrue(counter < lobbyGetDTO.getPlayers().size());
            // Get the next user from the lobby
            // Check that their id, username and ready status matches
            assertEquals(lobbyGetDTO.getPlayers().get(counter).getId(), player.getId());
            assertEquals(lobbyGetDTO.getPlayers().get(counter).getName(), player.getName());
            assertEquals(lobbyGetDTO.getPlayers().get(counter).isReady(), player.isReady());
            assertEquals(lobbyGetDTO.getPlayers().get(counter).getTeam(), player.getTeam().getTeamNumber());
            ++counter;
        }

        assertEquals(lobbyGetDTO.getVisibility(), LOBBY.getVisibility());
        assertEquals(lobbyGetDTO.getGameMode(), LOBBY.getGameMode());
        assertEquals(lobbyGetDTO.getGameType(), LOBBY.getGameType());
        assertEquals(lobbyGetDTO.getInvitationCode(), LOBBY.getInvitationCode());
    }

    @Test
    void testGetLobby_fromPlayer_toPlayerGetDTO_success() {
        // create Lobby
        IPlayer player = new Player(1L, "username", "token", Team.RED);

        // MAP -> LobbyGetDTO
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertIPlayerToPlayerGetDTO(player);

        // check content
        assertEquals(player.getId(), playerGetDTO.getId());
        assertEquals(player.getName(), playerGetDTO.getName());
        assertEquals(player.isReady(), playerGetDTO.isReady());
        assertEquals(player.getTeam().getTeamNumber(), playerGetDTO.getTeam());
    }

    @Test
    void testGetLobby_host() {
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(LOBBY, LOBBY.getHost().getToken());

        assertEquals(LOBBY.getInvitationCode(), lobbyGetDTO.getInvitationCode());
    }

    @Test
    void testGetLobby_noHost() {
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(LOBBY, "WrongToken");

        assertNull(lobbyGetDTO.getInvitationCode());
    }
}
