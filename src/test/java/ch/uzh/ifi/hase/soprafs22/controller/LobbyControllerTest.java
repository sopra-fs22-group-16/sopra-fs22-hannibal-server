package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @Test
    void givenLobbies_whenGetLobby_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);


        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.getLobby(lobby.getHost().getToken(), 1L)).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/game/lobby/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", lobby.getHost().getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)lobby.getId())))
                .andExpect(jsonPath("$.name", is(lobby.getName())))
                .andExpect(jsonPath("$.hostId", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.players[0].id", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.players[0].team", is(lobby.getHost().getTeam().getTeamNumber())))
                .andExpect(jsonPath("$.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.invitationCode", is(lobby.getInvitationCode())));
    }

    @Test
    void  unregistered_createLobby_validInput_lobbyCreated_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName(lobby.getName());
        lobbyPostDTO.setVisibility(lobby.getVisibility());
        lobbyPostDTO.setGameMode(lobby.getGameMode());
        lobbyPostDTO.setGameType(lobby.getGameType());


        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.createLobby("", lobby.getName(), lobby.getVisibility(), lobby.getGameMode(), lobby.getGameType())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("token", "");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobby.id", is((int)lobby.getId())))
                .andExpect(jsonPath("$.lobby.name", is(lobby.getName())))
                .andExpect(jsonPath("$.lobby.hostId", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].id", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.lobby.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.lobby.players[0].team", is(lobby.getHost().getTeam().getTeamNumber())))
                .andExpect(jsonPath("$.lobby.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.lobby.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.lobby.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.lobby.invitationCode", is(lobby.getInvitationCode())))
                .andExpect(jsonPath("$.token", is(lobby.getHost().getToken())));

    }

    @Test
    void  registered_createLobby_validInput_lobbyCreated_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName(lobby.getName());
        lobbyPostDTO.setVisibility(lobby.getVisibility());
        lobbyPostDTO.setGameMode(lobby.getGameMode());
        lobbyPostDTO.setGameType(lobby.getGameType());


        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.createLobby("registeredUserToken", lobby.getName(), lobby.getVisibility(), lobby.getGameMode(), lobby.getGameType())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("token", "registeredUserToken");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobby.id", is((int)lobby.getId())))
                .andExpect(jsonPath("$.lobby.name", is(lobby.getName())))
                .andExpect(jsonPath("$.lobby.hostId", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].id", is((int)lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.lobby.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.lobby.players[0].team", is(lobby.getHost().getTeam().getTeamNumber())))
                .andExpect(jsonPath("$.lobby.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.lobby.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.lobby.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.lobby.invitationCode", is(lobby.getInvitationCode())));

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
