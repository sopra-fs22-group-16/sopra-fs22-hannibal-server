package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @Test
    public void givenLobbies_whenGetLobby_thenReturnJsonArray() throws Exception {
        // given
        IUser host = new User();
        ILobby lobby = LobbyManager.getInstance().createLobby(host);

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.getLobby("token", 1L)).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/game/lobby/1").contentType(MediaType.APPLICATION_JSON).header("token", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyId", is((int)lobby.getId())))
                .andExpect(jsonPath("$.name", is(lobby.getName())))
                .andExpect(jsonPath("$.owner", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.members[0].id", is((int)host.getId())))
                .andExpect(jsonPath("$.members[0].name", is(host.getUsername())))
                .andExpect(jsonPath("$.members[0].ready", is(false)))
                .andExpect(jsonPath("$.visibility", is(lobby.getLobbyMode().toString())))
                .andExpect(jsonPath("$.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.ranked", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.invitationCode").hasJsonPath());
                //TODO: Add check for invitation code

    }
}
