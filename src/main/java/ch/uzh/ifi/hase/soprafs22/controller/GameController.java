package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.rest.dto.AttackPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.WaitPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{apiVersion}/game/match/{id}/command/attack")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attack(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody AttackPostDTO attackPostDTO) {
        Position attacker = DTOMapper.INSTANCE.convertPositionDTOToPosition(attackPostDTO.getAttacker());
        Position defender = DTOMapper.INSTANCE.convertPositionDTOToPosition(attackPostDTO.getDefender());

        gameService.attack(id, token, attacker, defender);
    }

    @PostMapping("/{apiVersion}/game/match/{id}/command/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void move(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody MovePostDTO movePostDTO) {
        Position start = DTOMapper.INSTANCE.convertPositionDTOToPosition(movePostDTO.getStart());
        Position end = DTOMapper.INSTANCE.convertPositionDTOToPosition(movePostDTO.getEnd());

        gameService.move(id, token, start, end);
    }

    @PostMapping("/{apiVersion}/game/match/{id}/command/wait")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unitWait(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody WaitPostDTO waitPostDTO) {
        Position unitPosition = DTOMapper.INSTANCE.convertPositionDTOToPosition(waitPostDTO.getUnitPosition());

        gameService.unitWait(id, token, unitPosition);
    }
}
