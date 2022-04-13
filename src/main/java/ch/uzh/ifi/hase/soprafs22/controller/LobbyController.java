package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.LobbyService;
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
    private final static String API_VERSION = "v1";
    private final LobbyService lobbyService;

    @Autowired
    SimpMessagingTemplate socketMessage;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/{API_VERSION}/game/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Map<String, Object> createLobby(@RequestHeader("token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {

        // Get data from LobbyPostDTO
        String name = lobbyPostDTO.getName();
        Visibility visibility = lobbyPostDTO.getVisibility();
        GameMode gameMode = lobbyPostDTO.getGameMode();
        GameType gameType= lobbyPostDTO.getGameType();

        // Create a new lobby for user with this token
        ILobby lobby = lobbyService.createLobby(token, name, visibility, gameMode, gameType);

        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby);

        // Construct return value
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("lobby", lobbyGetDTO);
        if(token == null || token.isEmpty())
            returnMap.put("token", lobby.getHost().getToken());

        return returnMap;
    }

    @GetMapping("/{API_VERSION}/game/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobby(@RequestHeader("token") String token, @PathVariable Long id) {

        ILobby lobby = lobbyService.getLobby(token, id);

        return DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby);
    }

    @PutMapping("/{API_VERSION}/game/lobby/{id}/player")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // TODO add tests.
    public void modifyPlayerInLobby(@RequestHeader("token") String token, @PathVariable Long id, PlayerPutDTO playerPutDTO) {
        //lobby id --> id
        lobbyService.modifyPlayer(token, id, playerPutDTO.getName(), playerPutDTO.getReady());
    }

    @PutMapping("/{API_VERSION}/game/lobby/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateLobby(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody LobbyPostDTO lobbyPutDTO) {

        ILobby lobby = lobbyService.getLobby(token, id);

        // Get data from LobbyPostDTO
        String name = lobbyPutDTO.getName();
        Visibility visibility = lobbyPutDTO.getVisibility();
        GameMode gameMode = lobbyPutDTO.getGameMode();
        GameType gameType= lobbyPutDTO.getGameType();

        lobbyService.updateLobby(lobby, token, name, visibility, gameMode, gameType);

        // send message to client via socket
        socketMessage.convertAndSend("/topic/lobby/" + id, "");
    }

    @GetMapping("/{API_VERSION}/game/lobby/{id}/qrcode")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getLobbyQRCode(@RequestHeader("token") String token, @PathVariable Long id){

        byte[] qrCode = lobbyService.getQRCodeFromLobby(token, id);

        return Base64.getEncoder().encodeToString(qrCode);
    }

    @GetMapping("/{API_VERSION}/game/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getLobby() {
        Collection<ILobby> lobbiesCollection = lobbyService.getLobbiesCollection();
        List<LobbyGetDTO> lobbiesGetDTOs = new ArrayList();
        Iterator<ILobby> iteratorLobbies = lobbiesCollection.iterator();

        while(iteratorLobbies.hasNext()) {
            ILobby lobby = iteratorLobbies.next();
            if(lobby.getVisibility()== Visibility.PUBLIC) {
                lobbiesGetDTOs.add(DTOMapper.INSTANCE.convertILobbyToLobbyGetDTO(lobby));
            }
        }

        return lobbiesGetDTOs;

    }
}
