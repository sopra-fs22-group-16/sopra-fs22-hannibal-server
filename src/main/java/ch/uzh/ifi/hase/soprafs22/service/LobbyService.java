package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
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
     */
    public ILobby createLobby(String token){

        IUser host;

        // Check if token is set, then find user with token and set him as admin
        // ToDo: Create User with unique id etc.
        if(token.isEmpty()){
            host = new User();
        }else{
            // Create user with registered user info
            host = new User();
        }


        ILobby newLobby;

        // Try to create a new lobby
        try {
            newLobby = LobbyManager.getInstance().createLobby(host);
        }catch(SmallestLobbyIdNotCreatable e){
           e.printStackTrace();
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The server could not generate a unique id");
        }

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }


    // TODO: Implement getLobby
    /**
     * Find and return lobby with specified id using the LobbyManager
     * @param lobbyId the id of the lobby to look up
     * @return the lobby with the specified id
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if there is no lobby with the specified lobbyId
     * @throws ResponseStatusException with HttpStatus.UNAUTHORIZED if the user is not in the lobby and thus shouldn't have access
     */
    public ILobby getLobby(String token, Long lobbyId){

        ILobby lobby = LobbyManager.getInstance().getLobbyWithId(lobbyId);

        // Check if lobby exists
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

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("The user is not authorized to get the lobby with id %s", lobbyId));
    }

}
