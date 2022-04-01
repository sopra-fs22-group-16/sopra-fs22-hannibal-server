package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

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
        ILobby lobby = LobbyManager.getInstance().createLobby("LobbyName", LobbyMode.PRIVATE, host);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);


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
                .andExpect(jsonPath("$.invitationCode", is(lobby.getInvitationCode())));
    }

    @Test
    public void  unregistered_createLobby_validInput_lobbyCreated_thenReturnJsonArray() throws Exception {
        // given
        IUser host = new User();
        ILobby lobby = LobbyManager.getInstance().createLobby("LobbyName", LobbyMode.PRIVATE, host);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName(lobby.getName());
        lobbyPostDTO.setLobbyMode(lobby.getLobbyMode());
        lobbyPostDTO.setGameMode(lobby.getGameMode());
        lobbyPostDTO.setGameType(lobby.getGameType());


        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.createLobby("", lobby.getName(), lobby.getLobbyMode(), lobby.getGameMode(), lobby.getGameType())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("token", "");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobbyId", is((int)lobby.getId())))
                .andExpect(jsonPath("$.name", is(lobby.getName())))
                .andExpect(jsonPath("$.owner", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.members[0].id", is((int)host.getId())))
                .andExpect(jsonPath("$.members[0].name", is(host.getUsername())))
                .andExpect(jsonPath("$.members[0].ready", is(false)))
                .andExpect(jsonPath("$.visibility", is(lobby.getLobbyMode().toString())))
                .andExpect(jsonPath("$.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.ranked", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.invitationCode", is(lobby.getInvitationCode())));

    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object object to be mapped
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }

}
