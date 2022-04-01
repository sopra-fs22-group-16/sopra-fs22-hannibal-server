package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import ch.uzh.ifi.hase.soprafs22.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // TODO: Implement createLobby
    /**
     * Create a new lobby and add it to the LobbyManager
     * @return the created lobby
     * @throws ResponseStatusException with HttpStatus.INTERNAL_SERVER_ERROR if the lobbyManager wasn't able to generate a new id
     * @throws ResponseStatusException with HttpStatus.CONFLICT if the provided lobby name is not unique
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if the provided information is incomplete
     * @throws ResponseStatusException with HttpStatus.FORBIDDEN if no registered user was found with the provided token
     *
     */
    public ILobby createLobby(String token, String lobbyName, LobbyMode lobbyMode, GameMode gameMode, GameType gameType){

        // Check if values are valid
        if(lobbyName == null || lobbyName.isEmpty()){
            String errorMessage = "The lobby name provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(lobbyMode == null){
            String errorMessage = "The lobby mode provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(gameMode == null){
            String errorMessage = "The game mode provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }else if(gameType == null){
            String errorMessage = "The game type provided is empty. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        IUser host;

        // Check if token is set, then find user with token and set him as admin
        // ToDo: Create User with unique id etc.
        if(token == null || token.isEmpty()){
            host = new User();
        }else{
            // Create user with registered user info
            host = new User();
            if(host == null){
                String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be created!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
        }

        // Check if lobby name already exists
        if(LobbyManager.getInstance().getLobbyWithName(lobbyName) != null){
            String errorMessage = "The lobby name provided is not unique. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        ILobby newLobby;

        // Try to create a new lobby
        try {
            newLobby = LobbyManager.getInstance().createLobby(lobbyName, lobbyMode, host);
        }catch(SmallestLobbyIdNotCreatable e){
           e.printStackTrace();
           String errorMessage = "The server could not generate a unique id";
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        }

        // Set the gameType and gameMode of the lobby
        newLobby.setGameMode(gameMode);
        newLobby.setGameType(gameType);

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

        ILobby lobby = LobbyManager.getInstance().getLobbyWithId(lobbyId);

        // Check if lobby exists else throw an error
        if(lobby == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The lobby with the id %d was not found", lobbyId));
        }

        // Check if user is in lobby
        for(IUser user: lobby){
            // If tokens match return the lobby
            if(user.getToken().equals(token)){
                return lobby;
            }
        }

        // If no user was found matching the token throw an error
        String errorMessage = "The provided authentication was incorrect.";
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }

}
