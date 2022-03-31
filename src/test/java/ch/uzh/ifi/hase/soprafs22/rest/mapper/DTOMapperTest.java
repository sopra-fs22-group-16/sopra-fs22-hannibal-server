package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateLobby_fromLobby_toLobbyGetDTO_success() {

        // create Host
        User host = new User();
        host.setId(1L);
        host.setToken("token123");
        host.setUsername("myUsername");

        // create Lobby
        Lobby lobby = new Lobby(1L,"MyLobbyName", LobbyMode.PRIVATE, host);

        // MAP -> LobbyGetDTO
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertLobbyToLobbyGetDTO(lobby);

        // check content
        assertEquals(lobbyGetDTO.getLobbyId(), lobby.getId());
        assertEquals(lobbyGetDTO.getName(), lobby.getName());
        assertEquals(lobbyGetDTO.getOwner(), lobby.getHost().getId());
        assertEquals(lobbyGetDTO.getMembers().get(0).getId(), lobby.getUserList().get(0).getId());
        assertEquals(lobbyGetDTO.getMembers().get(0).getName(), lobby.getUserList().get(0).getUsername());
        // assertEquals(lobbyGetDTO.getMembers().get(0).isReady(), lobby.isUserReady(lobby.getUserList().get(0)));
        assertEquals(lobbyGetDTO.getVisibility(), lobby.getLobbyMode());
        // assertEquals(lobbyGetDTO.getGameMode(), lobby.getGame().getGameMode());
        // assertEquals(lobbyGetDTO.getRanked(), lobby.getGame().getGameType());
    }
}
