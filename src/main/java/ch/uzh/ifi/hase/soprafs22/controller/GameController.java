package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameGetDTO;
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

    private final LobbyService lobbyService;
    private final GameService gameService;

    GameController(LobbyService lobbyService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }

    @GetMapping("/{apiVersion}/game/match/{lobbyId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO getInitialMatch(@RequestHeader("token") String token, @PathVariable Long lobbyId) {
        ILobby lobby = lobbyService.getLobby(token, lobbyId);

        GameType gameType = lobby.getGameType();
        GameMode gameMode = lobby.getGameMode();

        Game game = gameService.getInitialGame(gameType,gameMode);

        return DTOMapper.INSTANCE.convertGameToGameGetDTO(game);
    }
}
