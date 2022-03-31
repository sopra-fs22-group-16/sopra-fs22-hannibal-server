package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreateable;
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
        host = new User();

        ILobby lobby;

        // Try to create a new lobby
        try {
            lobby = LobbyManager.getInstance().createLobby(host);
        }catch(SmallestLobbyIdNotCreateable e){
           e.printStackTrace();
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The server could not generate a unique id");
        }

        return lobby;
    }


    // TODO: Implement getLobby
    /**
     * Find and return lobby with specified id using the LobbyManager
     * @param lobbyId the id of the lobby to look up
     * @return the lobby with the specified id
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if there is no lobby with the specified lobbyId
     */
    public void getLobby(Long lobbyId){

    }

}
