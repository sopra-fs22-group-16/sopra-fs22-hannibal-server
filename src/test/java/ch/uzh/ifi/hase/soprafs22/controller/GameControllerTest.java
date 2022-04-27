package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.rest.dto.AttackPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.WaitPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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

    private static final String TOKEN = "fakeToken";
    private static final long MATCH_ID = 101L;
    private PositionDTO positionDTO1;
    private PositionDTO positionDTO2;

    private Position position1;
    private Position position2;

    @BeforeEach
    void setUp() {
        position1 = new Position(1, 2);
        position2 = new Position(3, 4);
        positionDTO1 = new PositionDTO();
        positionDTO2 = new PositionDTO();

        positionDTO1.setX(1);
        positionDTO1.setY(2);
        positionDTO2.setX(3);
        positionDTO2.setY(4);
    }


    @Test
    void test_unitAttack() throws Exception {
        AttackPostDTO attackPostDTO = new AttackPostDTO();
        attackPostDTO.setAttacker(positionDTO1);
        attackPostDTO.setDefender(positionDTO2);
        MockHttpServletRequestBuilder request = post("/v1/game/match/101/command/attack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(attackPostDTO))
                .header("token", TOKEN);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        verify(gameService).unitAttack(eq(MATCH_ID), eq(TOKEN), eq(position1), eq(position2));
    }

    @Test
    void test_unitMove() throws Exception {
        MovePostDTO movePostDTO = new MovePostDTO();
        movePostDTO.setStart(positionDTO1);
        movePostDTO.setEnd(positionDTO2);
        MockHttpServletRequestBuilder request = post("/v1/game/match/101/command/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movePostDTO))
                .header("token", TOKEN);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        verify(gameService).unitMove(eq(MATCH_ID), eq(TOKEN), eq(position1), eq(position2));
    }

    @Test
    void test_unitWait() throws Exception {
        WaitPostDTO waitPostDTO = new WaitPostDTO();
        waitPostDTO.setUnitPosition(positionDTO1);
        MockHttpServletRequestBuilder request = post("/v1/game/match/101/command/wait")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(waitPostDTO))
                .header("token", TOKEN);

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        verify(gameService).unitWait(eq(MATCH_ID), eq(TOKEN), eq(position1));
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