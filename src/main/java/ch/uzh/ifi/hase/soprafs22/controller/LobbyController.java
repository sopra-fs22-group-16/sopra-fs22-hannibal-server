package ch.uzh.ifi.hase.soprafs22.controller;

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


    // TODO: Add request body as specified
    // TODO: Add implementation for creating lobby

    @PostMapping("/v1/game/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createLobby(@RequestHeader("token") String token) {

    }

    // TODO: Add implementation for get lobby with id

    @GetMapping("/v1/game/lobby/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void getLobby(@RequestHeader("token") String token, @PathVariable Long id) {

    }

}
