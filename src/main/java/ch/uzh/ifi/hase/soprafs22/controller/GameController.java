package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.GameDeltaPutDTO;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitCommandPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */

@RestController
public class GameController {
    @Value("${api.version}")
    private String apiVersion;

    private static final String TOPIC_GAME = "/topic/game/";  // Compliant

    @Autowired
    SimpMessagingTemplate socketMessage;

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/attack")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitAttack(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitCommandPutDTO unitCommandPutDTO) {
        Position attacker = DTOMapper.INSTANCE.convertPositionPutDTOToPosition(unitCommandPutDTO.getStart());
        Position defender = DTOMapper.INSTANCE.convertPositionPutDTOToPosition(unitCommandPutDTO.getEnd());

        List<Unit> affectedUnits = gameService.unitAttack(id, token, attacker, defender);
        // Movement delta for socket.
        UnitCommandPutDTO moveDeltaSock = new UnitCommandPutDTO();
        moveDeltaSock.setStart(unitCommandPutDTO.getStart());
        moveDeltaSock.setEnd(unitCommandPutDTO.getEnd());

        GameDeltaPutDTO deltaSock = new GameDeltaPutDTO();
        deltaSock.setMove(moveDeltaSock);
        List<HealthPutDTO> healthDTOs = new ArrayList<>();
        for (Unit unit : affectedUnits){
            // Health delta for socket.
            HealthPutDTO health = new HealthPutDTO();
            health.setHealth(unit.getHealth());
            health.setUnitPosition(DTOMapper.INSTANCE.convertPositionToPositionPutDTO(unit.getPosition()));
            healthDTOs.add(health);
        }
        if (healthDTOs.size() > 0)
            deltaSock.setHealth(healthDTOs);

        sendThroughSocket(id, deltaSock);
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/wait")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitWait(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitCommandPutDTO unitCommandPutDTO) {
        Position start = DTOMapper.INSTANCE.convertPositionPutDTOToPosition(unitCommandPutDTO.getStart());
        Position end = DTOMapper.INSTANCE.convertPositionPutDTOToPosition(unitCommandPutDTO.getEnd());

        gameService.unitWait(id, token, start, end);

        UnitCommandPutDTO moveDeltaSock = new UnitCommandPutDTO();
        moveDeltaSock.setStart(unitCommandPutDTO.getStart());
        moveDeltaSock.setEnd(unitCommandPutDTO.getEnd());

        GameDeltaPutDTO deltaSock = new GameDeltaPutDTO();
        deltaSock.setMove(moveDeltaSock);
        sendThroughSocket(id, deltaSock);
    }

    /**
     * All socket info should be sent through this method to ensure format consistency.
     */
    private void sendThroughSocket(long id, GameDeltaPutDTO gameDelta) {
        socketMessage.convertAndSend("/topic/game/" + id, gameDelta);
    }
}
