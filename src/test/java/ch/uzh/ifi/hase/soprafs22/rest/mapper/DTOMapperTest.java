package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateLobby_fromLobby_toLobbyGetDTO_success() {

        // create Lobby
        ILobby lobby = new Lobby(1L,"MyLobbyName", LobbyMode.PRIVATE);

        // MAP -> LobbyGetDTO
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby);

        // check content
        assertEquals(lobbyGetDTO.getLobbyId(), lobby.getId());
        assertEquals(lobbyGetDTO.getName(), lobby.getName());
        assertEquals(lobbyGetDTO.getOwner(), lobby.getHost().getPlayerId());

        int counter = 0;
        for (Player player : lobby) {
            // Check that there exists an element in the lobbyGetDTO
            assertTrue(counter < lobbyGetDTO.getMembers().size());
            // Get the next user from the lobby
            // Check that their id, username and ready status matches
            assertEquals(lobbyGetDTO.getMembers().get(counter).getId(), player.getPlayerId());
            assertEquals(lobbyGetDTO.getMembers().get(counter).getName(), player.getUsername());
            assertEquals(lobbyGetDTO.getMembers().get(counter).isReady(), player.isReady());
            ++counter;
        }

        assertEquals(lobbyGetDTO.getVisibility(), lobby.getLobbyMode());
        assertEquals(lobbyGetDTO.getGameMode(), lobby.getGameMode());
        assertEquals(lobbyGetDTO.getRanked(), lobby.getGameType());
        assertEquals(lobbyGetDTO.getInvitationCode(), lobby.getInvitationCode());
    }
}
