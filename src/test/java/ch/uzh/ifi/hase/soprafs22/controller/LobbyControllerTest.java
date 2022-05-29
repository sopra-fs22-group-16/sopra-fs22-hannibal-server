package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyDelta;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.post_dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.post_dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.LobbyDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

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
    private UserRepository userRepository;

    @MockBean
    SimpMessagingTemplate socketMessage;

    @Captor
    ArgumentCaptor<LobbyDeltaWebSocketDTO> lobbyDeltaSockDTOArgumentCaptor;

    private static final long LOBBY_ID = 123L;

    @Test
    void givenLobbies_whenGetLobby_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
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
                .andExpect(jsonPath("$.id", is((int) lobby.getId())))
                .andExpect(jsonPath("$.name", is(lobby.getName())))
                .andExpect(jsonPath("$.hostId", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.players[0].id", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.players[0].team", is(lobby.getHost().getTeam().ordinal())))
                .andExpect(jsonPath("$.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.invitationCode", is(lobby.getInvitationCode())));
    }

    @Test
    void get_LobbiesCollection_returns_public_only() throws Exception {
        // given one private and one public
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.UNRANKED);

        ILobby lobby1 = new Lobby(1L, "lobbyName1", Visibility.PUBLIC, null);
        lobby1.setGameMode(GameMode.ONE_VS_ONE);
        lobby1.setGameType(GameType.UNRANKED);

        // merge into collection
        Map<Long, ILobby> lobbyMap = new HashMap<>();
        lobbyMap.put(lobby.getId(), lobby);
        lobbyMap.put(lobby1.getId(), lobby1);
        Collection<ILobby> lobbyCollection = lobbyMap.values();

        // mock lobby service that returns both public and private
        given(lobbyService.getLobbies()).willReturn(lobbyCollection);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/game/lobby")
                .contentType(MediaType.APPLICATION_JSON);

        // then the first lobby in the collection will be the public lobby
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) lobby1.getId())))
                .andExpect(jsonPath("$[0].name", is(lobby1.getName())))
                .andExpect(jsonPath("$[0].hostId", is((int) lobby1.getHost().getId())))
                .andExpect(jsonPath("$[0].players[0].id", is((int) lobby1.getHost().getId())))
                .andExpect(jsonPath("$[0].players[0].name", is(lobby1.getHost().getName())))
                .andExpect(jsonPath("$[0].players[0].ready", is(lobby1.getHost().isReady())))
                .andExpect(jsonPath("$[0].players[0].team", is(lobby1.getHost().getTeam().ordinal())))
                .andExpect(jsonPath("$[0].visibility", is(lobby1.getVisibility().toString())))
                .andExpect(jsonPath("$[0].gameMode", is(lobby1.getGameMode().toString())))
                .andExpect(jsonPath("$[0].gameType", is(lobby1.getGameType().toString())));
    }


    @Test
    void unregistered_createLobby_validInput_lobbyCreated_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
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
                .andExpect(jsonPath("$.lobby.id", is((int) lobby.getId())))
                .andExpect(jsonPath("$.lobby.name", is(lobby.getName())))
                .andExpect(jsonPath("$.lobby.hostId", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].id", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.lobby.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.lobby.players[0].team", is(lobby.getHost().getTeam().ordinal())))
                .andExpect(jsonPath("$.lobby.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.lobby.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.lobby.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.lobby.invitationCode", is(lobby.getInvitationCode())))
                .andExpect(jsonPath("$.token", is(lobby.getHost().getToken())))
                .andExpect(jsonPath("$.playerId", is((int) lobby.getHost().getId())));

    }

    @Test
    void givenPlayerPutDTO_validPlayerModification_thenReturnLobbyDelta() throws Exception {
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);
        IPlayer player = lobby.generatePlayer();
        lobby.addPlayer(player);

        PlayerPutDTO playerPutDTO = new PlayerPutDTO();
        playerPutDTO.setName("Luis");
        playerPutDTO.setReady(true);

        // when
        doNothing().when(lobbyService).modifyPlayer(player.getToken(), LOBBY_ID, playerPutDTO.getName(), playerPutDTO.getReady());

        MockHttpServletRequestBuilder putRequest = put("/v1/game/lobby/" + lobby.getId() + "/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPutDTO))
                .header("token", player.getToken());

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(lobbyService).modifyPlayer(player.getToken(), LOBBY_ID, playerPutDTO.getName(), playerPutDTO.getReady());
        verify(socketMessage).convertAndSend(eq("/topic/lobby/123"), lobbyDeltaSockDTOArgumentCaptor.capture());

        LobbyDeltaWebSocketDTO deltaSockDTO = lobbyDeltaSockDTOArgumentCaptor.getValue();
        assertTrue(deltaSockDTO.isPullUpdate());
        assertFalse(deltaSockDTO.isRedirectToGame());
        assertNull(deltaSockDTO.getRemovedPlayerIdList());
        assertNull(deltaSockDTO.getNameChangedOfPlayerWithId());
    }

    @Test
    void givenLobbyId_validGameCreation_thenReturnLobbyDelta() throws Exception {
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);
        IPlayer player = lobby.generatePlayer();
        lobby.addPlayer(player);

        Map<String, IPlayer> decoratedPlayers = new HashMap<>();
        decoratedPlayers.put(player.getToken(), player);

        Game game = new Game(GameMode.ONE_VS_ONE, GameType.RANKED, decoratedPlayers);

        // when
        given(lobbyService.createGame(player.getToken(), lobby.getId())).willReturn(game);

        MockHttpServletRequestBuilder putRequest = post("/v1/game/match/"+LOBBY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", player.getToken());

        mockMvc.perform(putRequest)
                .andExpect(status().isCreated());

        verify(lobbyService).createGame(player.getToken(), lobby.getId());
        verify(socketMessage).convertAndSend(eq("/topic/lobby/123"), lobbyDeltaSockDTOArgumentCaptor.capture());

        LobbyDeltaWebSocketDTO deltaSockDTO = lobbyDeltaSockDTOArgumentCaptor.getValue();
        assertFalse(deltaSockDTO.isPullUpdate());
        assertTrue(deltaSockDTO.isRedirectToGame());
        assertNull(deltaSockDTO.getRemovedPlayerIdList());
        assertNull(deltaSockDTO.getNameChangedOfPlayerWithId());
    }

    @Test
    void givenLobbyPostDTO_validLobbyUpdate_thenReturnLobbyDelta() throws Exception {
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);
        IPlayer player = lobby.generatePlayer();
        lobby.addPlayer(player);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName("otherName");
        lobbyPostDTO.setVisibility(Visibility.PUBLIC);
        lobbyPostDTO.setGameMode(GameMode.TWO_VS_TWO);
        lobbyPostDTO.setGameType(GameType.UNRANKED);

        // when
        doNothing().when(lobbyService).updateLobby(lobby, player.getToken(), lobbyPostDTO.getName(), lobbyPostDTO.getVisibility(), lobbyPostDTO.getGameMode(), lobbyPostDTO.getGameType());

        MockHttpServletRequestBuilder putRequest = put("/v1/game/lobby/" + lobby.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("token", player.getToken());

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(lobbyService).updateLobby(null, player.getToken(), lobbyPostDTO.getName(), lobbyPostDTO.getVisibility(), lobbyPostDTO.getGameMode(), lobbyPostDTO.getGameType());
        verify(socketMessage).convertAndSend(eq("/topic/lobby/"+ lobby.getId()), lobbyDeltaSockDTOArgumentCaptor.capture());
        LobbyDeltaWebSocketDTO deltaSockDTO1 = lobbyDeltaSockDTOArgumentCaptor.getValue();
        assertNull(deltaSockDTO1.getNameChangedOfPlayerWithId());
        assertTrue(deltaSockDTO1.isPullUpdate());
        assertFalse(deltaSockDTO1.isRedirectToGame());
        assertEquals(0, deltaSockDTO1.getRemovedPlayerIdList().size());

        verify(socketMessage).convertAndSend(eq("/topic/lobby/join"), lobbyDeltaSockDTOArgumentCaptor.capture());
        Object deltaSockDTO2 = lobbyDeltaSockDTOArgumentCaptor.getValue();
        assertEquals("", deltaSockDTO2);
    }

    @Test
    void registered_createLobby_validInput_lobbyCreated_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);
        String token = lobby.getHost().getToken();

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName(lobby.getName());
        lobbyPostDTO.setVisibility(lobby.getVisibility());
        lobbyPostDTO.setGameMode(lobby.getGameMode());
        lobbyPostDTO.setGameType(lobby.getGameType());

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.createLobby(token, lobby.getName(), lobby.getVisibility(), lobby.getGameMode(), lobby.getGameType())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO))
                .header("token", token);

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobby.id", is((int) lobby.getId())))
                .andExpect(jsonPath("$.lobby.name", is(lobby.getName())))
                .andExpect(jsonPath("$.lobby.hostId", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].id", is((int) lobby.getHost().getId())))
                .andExpect(jsonPath("$.lobby.players[0].name", is(lobby.getHost().getName())))
                .andExpect(jsonPath("$.lobby.players[0].ready", is(lobby.getHost().isReady())))
                .andExpect(jsonPath("$.lobby.players[0].team", is(lobby.getHost().getTeam().ordinal())))
                .andExpect(jsonPath("$.lobby.visibility", is(lobby.getVisibility().toString())))
                .andExpect(jsonPath("$.lobby.gameMode", is(lobby.getGameMode().toString())))
                .andExpect(jsonPath("$.lobby.gameType", is(lobby.getGameType().toString())))
                .andExpect(jsonPath("$.lobby.invitationCode", is(lobby.getInvitationCode())))
                .andExpect(jsonPath("$.token", is(lobby.getHost().getToken())))
                .andExpect(jsonPath("$.playerId", is((int) lobby.getHost().getId())));
    }

    @Test
    void unregistered_addPlayer_validInput_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.UNRANKED);
        String token = lobby.getHost().getToken();

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setInvitationCode(lobby.getInvitationCode());

        IPlayer player = lobby.generatePlayer();

        LobbyDelta lobbyDelta = new LobbyDelta(player, null);

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.addPlayer(lobby.getInvitationCode(), lobby.getId(), "")).willReturn(lobbyDelta);
        given(lobbyService.addPlayer(lobby.getInvitationCode(), lobby.getId(), null)).willReturn(lobbyDelta);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby/" + lobby.getId() + "/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO))
                .header("token", "");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) player.getId())))
                .andExpect(jsonPath("$.name", is(player.getName())))
                .andExpect(jsonPath("$.ready", is(player.isReady())))
                .andExpect(jsonPath("$.team", is(player.getTeam().ordinal())))
                .andExpect(jsonPath("$.token", is(player.getToken())));
    }

    @Test
    void registered_addPlayer_validInput_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.UNRANKED);
        String token = lobby.getHost().getToken();

        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setInvitationCode(lobby.getInvitationCode());

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(LOBBY_ID);
        registeredUser.setUsername("user");
        registeredUser.setPassword("password");
        registeredUser.setToken(UUID.randomUUID().toString());
        registeredUser.setRankedScore(1000);
        registeredUser.setLosses(0);
        registeredUser.setWins(99);

        IPlayer player = lobby.generatePlayer();
        player.linkRegisteredUser(registeredUser);

        LobbyDelta lobbyDelta = new LobbyDelta(player, null);

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.addPlayer(lobby.getInvitationCode(), lobby.getId(), registeredUser.getToken())).willReturn(lobbyDelta);

        // when
        MockHttpServletRequestBuilder postRequest = post("/v1/game/lobby/" + lobby.getId() + "/player")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO))
                .header("token", registeredUser.getToken());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) player.getId())))
                .andExpect(jsonPath("$.name", is(player.getName())))
                .andExpect(jsonPath("$.ready", is(player.isReady())))
                .andExpect(jsonPath("$.team", is(player.getTeam().ordinal())))
                .andExpect(jsonPath("$.token", is(player.getToken())));
    }

    @Test
    void validInput_getLobbyQRCode_thenReturnBase64() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
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
            qrCode[i] = (byte) val;
        }

        // this mocks the LobbyService -> we define above what the userService should
        // return when getUser() is called
        given(lobbyService.getQRCodeFromLobby("registeredUserToken", lobby.getId())).willReturn(qrCode);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/game/lobby/123/qrcode")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "registeredUserToken");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(qrCode))
                .andExpect(content().contentType(MediaType.valueOf("TEXT/PLAIN;CHARSET=UTF-8")));

    }

    @Test
    void createLobby_thenLeaveLobby() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.RANKED);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setName(lobby.getName());
        lobbyPostDTO.setVisibility(lobby.getVisibility());
        lobbyPostDTO.setGameMode(lobby.getGameMode());
        lobbyPostDTO.setGameType(lobby.getGameType());

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/v1/game/lobby/0/player")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "registeredUserToken");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

    }

    @Test
    void given1v1Game_whenGetGame_validInput_thenReturnJsonArray() throws Exception {
        // given
        ILobby lobby = new Lobby(LOBBY_ID, "lobbyName", Visibility.PRIVATE, null);
        lobby.setGameMode(GameMode.ONE_VS_ONE);
        lobby.setGameType(GameType.UNRANKED);

        IPlayer player1 = lobby.getHost();

        IPlayer player2 = lobby.generatePlayer();
        lobby.addPlayer(player2);
        lobby.setReady(player2.getToken(), true);

        lobby.startGame();

        Game game = lobby.getGame();

        given(lobbyService.getLobby(player1.getToken(), lobby.getId())).willReturn(lobby);

        // when
        MockHttpServletRequestBuilder getGameRequest = get("/v1/game/match/" + lobby.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", player1.getToken());

        // then
        mockMvc.perform(getGameRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameMode", is(game.getGameMode().toString())))
                .andExpect(jsonPath("$.gameType", is(game.getGameType().toString())))
                .andExpect(jsonPath("$.turnNumber", is(game.getTurnNumber())))
                .andExpect(jsonPath("$.playerIdCurrentTurn", is((int) game.getPlayerIdCurrentTurn())))
                .andExpect(jsonPath("$.players[" + '"' + player1.getId() + '"' + "].name", is(player1.getName())))
                .andExpect(jsonPath("$.players[" + '"' + player1.getId() + '"' + "].team", is(player1.getTeam().ordinal())))
                .andExpect(jsonPath("$.players[" + '"' + player2.getId() + '"' + "].name", is(player2.getName())))
                .andExpect(jsonPath("$.players[" + '"' + player2.getId() + '"' + "].team", is(player2.getTeam().ordinal())))
                .andExpect(jsonPath("$.gameMap").isNotEmpty())
                .andExpect(jsonPath("$.units").isNotEmpty());
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