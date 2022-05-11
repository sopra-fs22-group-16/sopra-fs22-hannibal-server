package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitAttackPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.GameDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.PositionPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitMovePutDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    SimpMessagingTemplate socketMessage;

    @Captor
    ArgumentCaptor<GameDeltaWebSocketDTO> gameDeltaSockDTOArgumentCaptor;

    private static final String TOKEN = "fakeToken";
    private static final long MATCH_ID = 101L;
    private PositionPutDTO positionPutDTO1;
    private PositionPutDTO positionPutDTO2;

    private Position position1;
    private Position position2;

    private AttackCommand attackCommand;
    private MoveCommand moveCommand;

    @BeforeEach
    void setUp() {
        position1 = new Position(1, 2);
        position2 = new Position(3, 4);
        positionPutDTO1 = new PositionPutDTO();
        positionPutDTO2 = new PositionPutDTO();

        positionPutDTO1.setX(1);
        positionPutDTO1.setY(2);
        positionPutDTO2.setX(3);
        positionPutDTO2.setY(4);

        attackCommand = new AttackCommand();
        attackCommand.setAttacker(position1);
        attackCommand.setDefender(position2);
        attackCommand.setAttackerDestination(position1);

        moveCommand = new MoveCommand();
        moveCommand.setStart(position1);
        moveCommand.setDestination(position2);
    }


    @Test
    void test_unitAttack() throws Exception {
        UnitAttackPutDTO attackPutDTO = new UnitAttackPutDTO();
        attackPutDTO.setAttacker(positionPutDTO1);
        attackPutDTO.setDefender(positionPutDTO2);
        attackPutDTO.setAttackerDestination(positionPutDTO1);

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
        when(gameService.unitAttack(any(), any(), any())).thenReturn(List.of(unit1, unit2));

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        //verify(gameService).unitAttack(MATCH_ID, TOKEN, attackCommand);
        verify(socketMessage).convertAndSend(eq("/topic/game/101"), gameDeltaSockDTOArgumentCaptor.capture());

        GameDeltaWebSocketDTO deltaSockDTO = gameDeltaSockDTOArgumentCaptor.getValue();

        assertNull(deltaSockDTO.getTurnInfo());
        UnitMovePutDTO moveSock = deltaSockDTO.getMove();
        assertEquals(1, moveSock.getStart().getX());
        assertEquals(2, moveSock.getStart().getY());
        //assertEquals(3, moveSock.getDestination().getX());
        //assertEquals(4, moveSock.getDestination().getY());

        List<HealthPutDTO> healthSock = deltaSockDTO.getHealth();
        assertEquals(2, healthSock.size());


        assertEquals(1, healthSock.get(0).getUnitPosition().getX());
        assertEquals(2, healthSock.get(0).getUnitPosition().getY());
        assertEquals(1, healthSock.get(0).getHealth());
        assertEquals(3, healthSock.get(1).getUnitPosition().getX());
        assertEquals(4, healthSock.get(1).getUnitPosition().getY());
        assertEquals(2, healthSock.get(1).getHealth());
    }

    @Test
    void test_unitMove() throws Exception {
        UnitMovePutDTO unitMovePutDTO = new UnitMovePutDTO();
        unitMovePutDTO.setStart(positionPutDTO1);
        unitMovePutDTO.setDestination(positionPutDTO2);
        MockHttpServletRequestBuilder request = put("/v1/game/match/101/command/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(unitMovePutDTO))
                .header("token", TOKEN);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        //verify(gameService).unitMove(MATCH_ID, TOKEN, moveCommand);
        verify(socketMessage).convertAndSend(eq("/topic/game/101"), gameDeltaSockDTOArgumentCaptor.capture());
        GameDeltaWebSocketDTO deltaSockDTO = gameDeltaSockDTOArgumentCaptor.getValue();

        assertNull(deltaSockDTO.getHealth());
        assertNull(deltaSockDTO.getTurnInfo());
        UnitMovePutDTO moveSock = deltaSockDTO.getMove();
        assertEquals(1, moveSock.getStart().getX());
        assertEquals(2, moveSock.getStart().getY());
        assertEquals(3, moveSock.getDestination().getX());
        assertEquals(4, moveSock.getDestination().getY());
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