package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lobby Service
 * This class is the "worker" and responsible for all functionality related to
 * the lobby
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final UserRepository userRepository;

    private final LobbyManager lobbyManager;

    @Autowired
    public LobbyService(UserRepository userRepository){
        this.userRepository = userRepository;
        this.lobbyManager = LobbyManager.getInstance();
    }

    /**
     * Create a new lobby and add it to the LobbyManager
     * @return the created lobby
     * @throws ResponseStatusException with HttpStatus.INTERNAL_SERVER_ERROR if the lobbyManager wasn't able to generate a new id
     * @throws ResponseStatusException with HttpStatus.CONFLICT if the provided lobby name is not unique
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if the provided information is incomplete
     * @throws ResponseStatusException with HttpStatus.FORBIDDEN if no registered user was found with the provided token
     *
     */
    public ILobby createLobby(String token, String lobbyName, Visibility visibility, GameMode gameMode, GameType gameType){

        // Check if values are valid
        if(lobbyName == null || lobbyName.isEmpty()){
            String errorMessage = "The lobby name provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(visibility == null){
            String errorMessage = "The lobby mode provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(gameMode == null){
            String errorMessage = "The game mode provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(gameType == null){
            String errorMessage = "The game type provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        RegisteredUser registeredUser = null;
        // Check if token is set, then find user with token and link him to the host player
        if(token != null && !token.isEmpty()){
            registeredUser = userRepository.findRegisteredUserByToken(token);
            if(registeredUser == null){
                String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be created!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
        }

        // Check if lobby name already exists
        if(lobbyManager.isLobbyNameInUse(lobbyName)){
            String errorMessage = "The lobby name provided is not unique. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        ILobby newLobby;

        // Try to create a new lobby
        try {
            newLobby = lobbyManager.createLobby(lobbyName, visibility);
        }catch(SmallestIdNotCreatable e){
           e.printStackTrace();
           String errorMessage = "The server could not generate a unique id";
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }

        // Set the gameType and gameMode of the lobby
        newLobby.setGameMode(gameMode);
        newLobby.setGameType(gameType);

        // Link the host to the registered user
        if(registeredUser != null){
           newLobby.getOwner().linkRegisteredUser(registeredUser);
        }

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }

    /**
     * Find and return lobby with specified id using the LobbyManager
     * @param lobbyId the id of the lobby to look up
     * @return the lobby with the specified id
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if there is no lobby with the specified lobbyId
     * @throws ResponseStatusException with HttpStatus.UNAUTHORIZED if no authentication was provided
     * @throws ResponseStatusException with HttpStatus.FORBIDDEN if no player was found in the lobby with the provided token
     */
    public ILobby getLobby(String token, Long lobbyId){

        // Check if authentication was provided
        if(token == null || token.isEmpty()){
            String errorMessage = "The user needs to provide authentication to retrieve lobby information.";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }

        ILobby lobby = lobbyManager.getLobbyWithId(lobbyId);

        // Check if lobby exists else throw an error
        if(lobby == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The lobby with the id %d was not found", lobbyId));
        }

        // Check if user is in lobby
        for(Player player: lobby){
            // If tokens match return the lobby
            if(player.getToken().equals(token)){
                return lobby;
            }
        }

        // If no user was found matching the token throw an error
        String errorMessage = "The provided authentication was incorrect.";
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }

    public void modifyPlayer(String token, Long lobbyId, PlayerPutDTO playerPutDTO) {
        lobbyManager.modifyPlayer(token, lobbyId, playerPutDTO);
    }
}
