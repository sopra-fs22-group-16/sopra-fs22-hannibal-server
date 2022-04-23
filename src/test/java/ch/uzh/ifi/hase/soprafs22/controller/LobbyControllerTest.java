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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    SimpMessagingTemplate socketMessage;

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

    @Test
    void  validInput_getLobbyQRCode_thenReturnBase64() throws Exception {
        // given
        ILobby lobby = new Lobby(0L, "lobbyName", Visibility.PRIVATE);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);

        // Sample qrCode png
        String hexByteCodeSampleImage = "89504e470d0a1a0a0000000d49484452000000640000006401030000004a2c071700000006504c5445ffffff00000055c2d37e000000097048597300000ec400000ec401952b0e1b0000015e49444154388d8dd44d8a84301005e0922cb24b5f40f01ad9e52a425f20bd6ffcb980b95276b946c00be82e8b60cdb39919e8812ea710e15b882f6595446f35725121f1468a39ff514f769d53fb28eb44b2ee8725a3d7c5a9502ec58b26efd67fa825ea26adae842c75d0d9b89f641f85f3ad8b3dafefd37e14aadba835bf8dfaa8f170d553eb757d2459dd82bc11f7757259d4133d989906d7319e93d437916e31df626bec85bce3c5d526e2f3ca52075aa5798e6a431649f71d410853c0932351bd21b5593e9c9a8bac961c798d0667ef643dc7d805ee42a4316551fd58a8411cbc44932835a361690d492d24ab37b6350e596a93648d9bc640757bc26867517d53da81aab78ce689ba875831085897c1caea1f7c2ef4cecc318b427128bc9f23230bbb929b526f650d4ca2b07f849e2d0e2b280b3b9d8d559bc6205c8a277a2db4bd541e3422bd264b12b260a6eab92b24eb3c9f27f2f80369596ff5054e213ebe412541f30000000049454e44ae426082";

        // Convert hex data into byte array
        byte[] qrCode = new byte[hexByteCodeSampleImage.length() / 2];
        for (int i = 0; i < qrCode.length; ++i) {
            int index = i * 2;

            // Using parseInt() method of Integer class
            int val = Integer.parseInt(hexByteCodeSampleImage.substring(index, index + 2), 16);
            qrCode[i] = (byte)val;
        }

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.getQRCodeFromLobby("registeredUserToken", lobby.getId())).willReturn(qrCode);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/game/lobby/0/qrcode")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "registeredUserToken");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(qrCode))
                .andExpect(content().contentType(MediaType.valueOf("TEXT/PLAIN;CHARSET=UTF-8")));

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
