package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.GameDelta;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitAttackPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitMoveDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.GameDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitHealthDTO;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    SimpMessagingTemplate socketMessage;

    @Captor
    ArgumentCaptor<GameDeltaWebSocketDTO> gameDeltaSockDTOArgumentCaptor;

    private static final String TOKEN = "fakeToken";
    private static final long MATCH_ID = 101L;
    private PositionDTO positionDTO1;
    private PositionDTO positionDTO2;
    private PositionDTO positionDTO3;


    private Position position1;
    private Position position2;
    private Position position3;

    private AttackCommand attackCommand;
    private MoveCommand moveCommand;

    private TurnInfo turnInfo = new TurnInfo(1, 2L);

    @BeforeEach
    void setUp() {
        position1 = new Position(1, 2);
        position2 = new Position(3, 4);
        position3 = new Position(5, 6);
        positionDTO1 = new PositionDTO();
        positionDTO2 = new PositionDTO();
        positionDTO3 = new PositionDTO();

        positionDTO1.setX(1);
        positionDTO1.setY(2);
        positionDTO2.setX(3);
        positionDTO2.setY(4);
        positionDTO3.setX(5);
        positionDTO3.setY(6);

        attackCommand = new AttackCommand(position1, position2, position3);

        moveCommand = new MoveCommand(position1, position2);
    }


    @Test
    void test_unitAttack() throws Exception {
        UnitAttackPutDTO attackPutDTO = new UnitAttackPutDTO();
        attackPutDTO.setAttacker(positionDTO1);
        attackPutDTO.setDefender(positionDTO2);
        attackPutDTO.setAttackerDestination(positionDTO3);

        MockHttpServletRequestBuilder request = put("/v1/game/match/101/command/attack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(attackPutDTO))
                .header("token", TOKEN);
        Unit unit1 = mock(Unit.class);
        when(unit1.getPosition()).thenReturn(position1);
        when(unit1.getHealth()).thenReturn(1);
        Unit unit2 = mock(Unit.class);
        when(unit2.getPosition()).thenReturn(position2);
        when(unit2.getHealth()).thenReturn(2);

        MoveCommand move = new MoveCommand(attackCommand.getAttacker(), attackCommand.getAttackerDestination());

        GameDelta gameDelta = new GameDelta(move, Map.of(position1, 1, position2, 2), /*turnInfo=*/ null, /*gameOverInfo=*/ null);
        when(gameService.unitAttack(any(), any(), any())).thenReturn(gameDelta);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        //verify(gameService).unitAttack(MATCH_ID, TOKEN, attackCommand);
        verify(socketMessage).convertAndSend(eq("/topic/game/101"), gameDeltaSockDTOArgumentCaptor.capture());

        GameDeltaWebSocketDTO deltaSockDTO = gameDeltaSockDTOArgumentCaptor.getValue();

        assertNull(deltaSockDTO.getTurnInfo());
        UnitMoveDTO moveSock = deltaSockDTO.getMove();
        assertEquals(1, moveSock.getStart().getX());
        assertEquals(2, moveSock.getStart().getY());
        assertEquals(5, moveSock.getDestination().getX());
        assertEquals(6, moveSock.getDestination().getY());

        List<UnitHealthDTO> healthSock = deltaSockDTO.getUnitHealths();
        assertEquals(2, healthSock.size());

        // list used to come from a list (with guaranteed order) now it is a map.
        for (UnitHealthDTO uh: healthSock) {
            if (uh.getHealth() == 1) {
                assertEquals(1, uh.getUnitPosition().getX());
                assertEquals(2, uh.getUnitPosition().getY());
                assertEquals(1, uh.getHealth());
            }
            else if (uh.getHealth() == 2) {
                assertEquals(3, uh.getUnitPosition().getX());
                assertEquals(4, uh.getUnitPosition().getY());
                assertEquals(2, uh.getHealth());
            }
            else
                fail();
        }
    }

    @Test
    void test_unitMove() throws Exception {
        UnitMoveDTO unitMoveDTO = new UnitMoveDTO();
        unitMoveDTO.setStart(positionDTO1);
        unitMoveDTO.setDestination(positionDTO2);
        MockHttpServletRequestBuilder request = put("/v1/game/match/101/command/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(unitMoveDTO))
                .header("token", TOKEN);
        GameDelta gameDelta = new GameDelta(moveCommand, /*turnInfo=*/ null, /*gameOver=*/ null);

        when(gameService.unitMove(any(), any(), any())).thenReturn(gameDelta);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        verify(gameService).unitMove(MATCH_ID, TOKEN, moveCommand);
        verify(socketMessage).convertAndSend(eq("/topic/game/101"), gameDeltaSockDTOArgumentCaptor.capture());
        GameDeltaWebSocketDTO deltaSockDTO = gameDeltaSockDTOArgumentCaptor.getValue();

        assertNull(deltaSockDTO.getUnitHealths());
        assertNull(deltaSockDTO.getTurnInfo());
        UnitMoveDTO moveSock = deltaSockDTO.getMove();
        assertEquals(1, moveSock.getStart().getX());
        assertEquals(2, moveSock.getStart().getY());
        assertEquals(3, moveSock.getDestination().getX());
        assertEquals(4, moveSock.getDestination().getY());
    }

    @Test
    void test_getStats() throws Exception {
        IGameStatistics gameStatistics = new IGameStatistics() {
            @Override
            public Map<Long, List<Integer>> unitsPerPlayer() {
                return Map.of(1L, List.of(6, 4, 2, 0), 2L, List.of(7, 5, 3, 1));
            }

            @Override
            public Map<Long, List<Integer>> killsPerPlayer() {
                return Map.of(1L, List.of(0, 1, 2, 3), 2L, List.of(4, 5, 6, 7));
            }

            @Override
            public float averageUnitsPerTurn() {
                return 0.42f;
            }

            @Override
            public float averageKillsPerTurn() {
                return 3.14f;
            }

            @Override
            public int totalMoves() {
                return 43;
            }
        };
        when(gameService.getGameStats(any(), any())).thenReturn(gameStatistics);
        MockHttpServletRequestBuilder getRequest = get("/v1/game/match/1/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "tuktuk");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unitsPerPlayer.1[0]", is(6)))
                .andExpect(jsonPath("$.unitsPerPlayer.1[1]", is(4)))
                .andExpect(jsonPath("$.unitsPerPlayer.1[2]", is(2)))
                .andExpect(jsonPath("$.unitsPerPlayer.1[3]", is(0)))
                .andExpect(jsonPath("$.unitsPerPlayer.2[0]", is(7)))
                .andExpect(jsonPath("$.unitsPerPlayer.2[1]", is(5)))
                .andExpect(jsonPath("$.unitsPerPlayer.2[2]", is(3)))
                .andExpect(jsonPath("$.unitsPerPlayer.2[3]", is(1)))
                .andExpect(jsonPath("$.killsPerPlayer.1[0]", is(0)))
                .andExpect(jsonPath("$.killsPerPlayer.1[1]", is(1)))
                .andExpect(jsonPath("$.killsPerPlayer.1[2]", is(2)))
                .andExpect(jsonPath("$.killsPerPlayer.1[3]", is(3)))
                .andExpect(jsonPath("$.killsPerPlayer.2[0]", is(4)))
                .andExpect(jsonPath("$.killsPerPlayer.2[1]", is(5)))
                .andExpect(jsonPath("$.killsPerPlayer.2[2]", is(6)))
                .andExpect(jsonPath("$.killsPerPlayer.2[3]", is(7)))
                .andExpect(jsonPath("$.averageUnitsPerTurn", is(0.42)))
                .andExpect(jsonPath("$.averageKillsPerTurn", is(3.14)))
                .andExpect(jsonPath("$.totalMoves", is(43)));
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