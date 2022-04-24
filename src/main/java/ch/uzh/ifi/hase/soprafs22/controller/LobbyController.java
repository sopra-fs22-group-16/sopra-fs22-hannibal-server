package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to
 * the lobby.
 * The controller will receive the request and delegate the execution to the
 * LobbyService and finally return the result.
 */

@RestController
public class LobbyController {
    @Value("${api.version}")
    private String apiVersion;

    private final LobbyService lobbyService;

    @Autowired
    SimpMessagingTemplate socketMessage;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/{apiVersion}/game/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Map<String, Object> createLobby(@RequestHeader("token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {

        // Get data from LobbyPostDTO
        String name = lobbyPostDTO.getName();
        Visibility visibility = lobbyPostDTO.getVisibility();
        GameMode gameMode = lobbyPostDTO.getGameMode();
        GameType gameType = lobbyPostDTO.getGameType();

        // Create a new lobby for user with this token
        ILobby lobby = lobbyService.createLobby(token, name, visibility, gameMode, gameType);
        if (token == null || token.isEmpty())
            token = lobby.getHost().getToken();

        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby, token);

        // Construct return value
        // TODO: We should return DTOs, not custom maps.
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("lobby", lobbyGetDTO);
        returnMap.put("token", token);
        returnMap.put("playerId", lobby.getHost().getId());

        return returnMap;
    }

    @GetMapping("/{apiVersion}/game/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobby(@RequestHeader("token") String token, @PathVariable Long id) {

        ILobby lobby = lobbyService.getLobby(token, id);

        return DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby, token);
    }

    @PutMapping("/{apiVersion}/game/lobby/{id}/player")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // TODO add tests.
    public void modifyPlayerInLobby(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody PlayerPutDTO playerPutDTO) {
        //lobby id --> id
        lobbyService.modifyPlayer(token, id, playerPutDTO.getName(), playerPutDTO.getReady());

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + id, "");
    }

    @PutMapping("/{apiVersion}/game/lobby/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateLobby(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody LobbyPostDTO lobbyPutDTO) {

        ILobby lobby = lobbyService.getLobby(token, id);

        // Get data from LobbyPostDTO
        String name = lobbyPutDTO.getName();
        Visibility visibility = lobbyPutDTO.getVisibility();
        GameMode gameMode = lobbyPutDTO.getGameMode();
        GameType gameType = lobbyPutDTO.getGameType();

        lobbyService.updateLobby(lobby, token, name, visibility, gameMode, gameType);

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + id, "");
    }

    @GetMapping("/{apiVersion}/game/lobby/{id}/qrcode")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getLobbyQRCode(@RequestHeader("token") String token, @PathVariable Long id) {

        byte[] qrCode = lobbyService.getQRCodeFromLobby(token, id);

        return Base64.getEncoder().encodeToString(qrCode);
    }

    @GetMapping("/{apiVersion}/game/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getLobby() {
        Collection<ILobby> lobbiesCollection = lobbyService.getLobbiesCollection();
        List<LobbyGetDTO> lobbiesGetDTOs = new ArrayList<>();

        for (ILobby lobby : lobbiesCollection) {
            if (lobby.getVisibility() == Visibility.PUBLIC) {
                lobbiesGetDTOs.add(DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby));
            }
        }

        return lobbiesGetDTOs;
    }

    @PostMapping("/{apiVersion}/game/match/{lobbyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGame(@RequestHeader("token") String token, @PathVariable Long lobbyId) {

        lobbyService.createGame(token, lobbyId);

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + lobbyId, "GameCreated");
    }

    @GetMapping("/{apiVersion}/game/match/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@RequestHeader("token") String token, @PathVariable Long lobbyId) {
        ILobby lobby = lobbyService.getLobby(token, lobbyId);

        Game game = lobby.getGame();

        return DTOMapper.INSTANCE.convertGameToGameGetDTO(game);

    }

    @DeleteMapping("/{apiVersion}/game/lobby/{id}/player")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveLobby(@RequestHeader("token") String token, @PathVariable Long id) {

        lobbyService.removePlayerFromLobby(token, id);

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + id, "");

    }

    @PostMapping("{apiVersion}/game/lobby/{id}/player")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO addPlayer(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody PlayerPostDTO playerPostDTO) {

        IPlayer newPlayer = lobbyService.addPlayer(playerPostDTO.getInvitationCode(), id);

        // Construct return value
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertIPlayerToPlayerGetDTO(newPlayer);

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + id, "");

        return playerGetDTO;
    }
}
