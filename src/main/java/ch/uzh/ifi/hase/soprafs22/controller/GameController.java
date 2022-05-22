package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.GameDelta;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.GameStatisticsGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitAttackPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitMoveDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.GameDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


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

    private static final String TOPIC_GAME = "/topic/game/";

    @Autowired
    SimpMessagingTemplate socketMessage;

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/attack")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitAttack(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitAttackPutDTO attackPutDTO) {
        AttackCommand attackCommand = DTOMapper.INSTANCE.convertUnitAttackPutDTOToAttackCommand(attackPutDTO);

        GameDelta gameDelta = gameService.unitAttack(id, token, attackCommand);

        sendThroughSocket(id, gameDelta);
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitMove(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody UnitMoveDTO movePutDTO) {
        MoveCommand moveCommand = DTOMapper.INSTANCE.convertUnitMovePutDTOToMoveCommand(movePutDTO);

        GameDelta gameDelta = this.gameService.unitMove(id, token, moveCommand);

        sendThroughSocket(id, gameDelta);
    }

    @GetMapping("/{apiVersion}/game/match/{id}/stats")
    public GameStatisticsGetDTO getGameStats(@RequestHeader("token") String token, @PathVariable Long id) {
        IGameStatistics gameStatistics =  this.gameService.getGameStats(id, token);
        return DTOMapper.INSTANCE.convertIGameStatisticsToGameStatisticsGetDTO(gameStatistics);
    }

    @PutMapping("/{apiVersion}/game/match/{id}/command/surrender")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void surrender(@RequestHeader("token") String token, @PathVariable Long id) {

        GameDelta gameDelta = this.gameService.surrender(id, token);

        sendThroughSocket(id, gameDelta);
    }

    /**
     * All socket info should be sent through this method to ensure format consistency.
     */
    private void sendThroughSocket(long id, GameDelta gameDelta) {
        GameDeltaWebSocketDTO unitMoveWebSocketDTO = DTOMapper.INSTANCE.convertGameDeltaToGameDeltaWebSocketDTO(gameDelta);
        this.socketMessage.convertAndSend(TOPIC_GAME + id, unitMoveWebSocketDTO);
    }
}
