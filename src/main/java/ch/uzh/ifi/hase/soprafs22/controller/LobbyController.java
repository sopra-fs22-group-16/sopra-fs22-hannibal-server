package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to
 * the lobby.
 * The controller will receive the request and delegate the execution to the
 * LobbyService and finally return the result.
 */

@RestController
public class LobbyController {

    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    // TODO: Add implementation for creating lobby

    @PostMapping("/v1/game/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestHeader("token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {

        // Get data from LobbyPostDTO
        String name = lobbyPostDTO.getName();
        LobbyMode lobbyMode = lobbyPostDTO.getLobbyMode();
        GameMode gameMode = lobbyPostDTO.getGameMode();
        GameType gameType= lobbyPostDTO.getGameType();

        // Create a new lobby for user with this token
        ILobby lobby = lobbyService.createLobby(token, name, lobbyMode, gameMode, gameType);

        return DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby);
    }

    @GetMapping("/v1/game/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobby(@RequestHeader("token") String token, @PathVariable Long id) {

        ILobby lobby = lobbyService.getLobby(token, id);

        return DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby);
    }

}
