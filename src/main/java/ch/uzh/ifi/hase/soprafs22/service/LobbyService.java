package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.EmptyUsernameException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
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
    public LobbyService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.lobbyManager = LobbyManager.getInstance();
    }

    /**
     * LobbyService constructor used for testing, to inject a mocked lobbyManager
     * @param userRepository the user repository
     * @param lobbyManager the mocked lobby manager
     */
    public LobbyService(UserRepository userRepository, LobbyManager lobbyManager) {
        this.userRepository = userRepository;
        this.lobbyManager = lobbyManager;
    }

    /**
     * Create a new lobby and add it to the LobbyManager
     *
     * @return the created lobby
     * @throws ResponseStatusException with HttpStatus.INTERNAL_SERVER_ERROR if the lobbyManager wasn't able to generate a new id
     * @throws ResponseStatusException with HttpStatus.CONFLICT if the provided lobby name is not unique
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if the provided information is incomplete
     * @throws ResponseStatusException with HttpStatus.FORBIDDEN if no registered user was found with the provided token
     */
    public ILobby createLobby(String token, String lobbyName, Visibility visibility, GameMode gameMode, GameType gameType) {

        // Check if values are valid
        checkStringConfigNullOrEmpty(lobbyName, "name","created");
        checkEnumConfigNull(visibility, "visibility", "created");
        checkEnumConfigNull(gameMode, "game mode", "created");
        checkEnumConfigNull(gameType, "game type", "created");

        RegisteredUser registeredUser = null;
        // Check if token is set, then find user with token and link him to the host player
        if (token != null && !token.isEmpty()) {
            registeredUser = userRepository.findRegisteredUserByToken(token);
            if (registeredUser == null) {
                String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be created!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
        }

        // Check if lobby name already exists
        if (lobbyManager.getLobbyWithName(lobbyName) != null) {
            String errorMessage = "The lobby name provided is not unique. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        ILobby newLobby;

        // Try to create a new lobby
        try {
            newLobby = lobbyManager.createLobby(lobbyName, visibility);
        }
        catch (SmallestIdNotCreatableException e) {
            String errorMessage = "The server could not generate a unique id";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, e);
        }

        // Set the gameType and gameMode of the lobby
        newLobby.setGameMode(gameMode);
        newLobby.setGameType(gameType);

        // Link the host to the registered user
        if (registeredUser != null) {
            newLobby.getHost().linkRegisteredUser(registeredUser);
        }

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }

    /**
     * Find and return lobby with specified id using the LobbyManager
     *
     * @param lobbyId the id of the lobby to look up
     * @return the lobby with the specified id
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if there is no lobby with the specified lobbyId
     * @throws ResponseStatusException with HttpStatus.UNAUTHORIZED if no authentication was provided
     * @throws ResponseStatusException with HttpStatus.FORBIDDEN if no player was found in the lobby with the provided token
     */
    public ILobby getLobby(String token, Long lobbyId) {

        checkStringConfigNullOrEmpty(token, "token", "accessed");

        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        checkUserIsInLobby(lobby, token, "accessed");

        return lobby;
    }

    public byte[] getQRCodeFromLobby(String token, Long lobbyId){
        checkStringConfigNullOrEmpty(token, "token", "accessed");

        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        checkUserIsInLobby(lobby, token, "accessed");

        try{
            return lobby.getQrCode();
        }catch (RestClientException e){
            String errorMessage = "The server received an invalid response from the upstream server.";
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, errorMessage, e);
        }

    }

    public void modifyPlayer(String token, Long lobbyId, String newName, Boolean ready) {
        try {
            lobbyManager.modifyPlayer(token, lobbyId, newName, ready);
        }
        catch (EmptyUsernameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username should not be empty.", e);
        }
        catch (PlayerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in lobby.", e);
        }
        catch (DuplicateUserNameInLobbyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + e.userName() + " is already taken.");
        }
    }

    public void updateLobby(ILobby lobby, String token, String lobbyName, Visibility visibility, GameMode gameMode, GameType gameType) {
        checkStringConfigNullOrEmpty(token, "token", "updated");
        if (!token.equals(lobby.getHost().getToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the host of the lobby.");
        }
        checkStringConfigNullOrEmpty(lobbyName, "name","updated");
        checkEnumConfigNull(visibility, "visibility", "updated");
        checkEnumConfigNull(gameMode, "game mode", "updated");
        checkEnumConfigNull(gameType, "game type", "updated");

        // Check if lobby name already exists
        if (lobbyManager.getLobbyWithName(lobbyName) != null && !lobbyManager.getLobbyWithName(lobbyName).equals(lobby)) {
            String errorMessage = "The lobby name provided is not unique. Therefore, the lobby could not be updated!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        if (!lobbyName.equals(lobby.getName())) {
            lobby.setName(lobbyName);
        }
        if (visibility != lobby.getVisibility()) {
            lobby.setVisibility(visibility);
        }
        if (gameMode != lobby.getGameMode()) {
            lobby.setGameMode(gameMode);
        }
        if (gameType != lobby.getGameType()) {
            lobby.setGameType(gameType);
        }
    }

    private void checkStringConfigNullOrEmpty(String s, String fieldName, String errorMessageEnding) {
        if(fieldName.equals("token")){
            if (s == null || s.isEmpty()) {
                String errorMessage = "The user needs to provide authentication to retrieve lobby information."
                        + "Therefore, the lobby could not be " + errorMessageEnding + "!";
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
            }
        }else{
            if (s == null || s.trim().isEmpty()) {
                String errorMessage = "The lobby " + fieldName + " provided is empty."
                        + "Therefore, the lobby could not be " + errorMessageEnding + "!";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        }
    }

    private void checkUserIsInLobby(ILobby lobby, String token, String errorMessageEnding) {
        // Check if user is in lobby
        for (Player player : lobby) {
            // If tokens match return true
            if (player.getToken().equals(token)) {
                return;
            }
        }
        String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be " + errorMessageEnding  + "!";
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }

    private <T extends Enum> void checkEnumConfigNull(T config, String configName, String errorMessageEnding) {
        if (config == null) {
            String errorMessage = "The " + configName + " provided is empty. Therefore, the lobby could not be " + errorMessageEnding + "!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private ILobby getLobbyByIdElseThrowNotFound(long lobbyId) {
        ILobby lobby = lobbyManager.getLobbyWithId(lobbyId);
        // Check if lobby exists else throw an error
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The lobby with the id %d was not found", lobbyId));
        }
        return lobby;
    }
}
