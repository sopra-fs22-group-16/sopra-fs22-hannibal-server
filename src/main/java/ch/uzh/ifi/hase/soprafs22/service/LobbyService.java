package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyDelta;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

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
    private final LobbyManager lobbyManager;
    private static final String CREATED = "created";
    private static final String ACCESSED = "accessed";
    private static final String TOKEN = "token";
    private static final String UPDATED = "updated";
    private final UserRepository userRepository;

    @Autowired
    public LobbyService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.lobbyManager = LobbyManager.getInstance();
    }

    /**
     * LobbyService constructor used for testing, to inject a mocked lobbyManager
     *
     * @param userRepository the user repository
     * @param lobbyManager   the mocked lobby manager
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
        checkStringConfigNullOrEmpty(lobbyName, "name", CREATED);
        checkEnumConfigNull(visibility, "visibility", CREATED);
        checkEnumConfigNull(gameMode, "game mode", CREATED);
        checkEnumConfigNull(gameType, "game type", CREATED);

        RegisteredUser registeredUser = null;
        // Check if token is set, then find user with token and link him to the host player
        if (token != null && !token.isEmpty()) {
            registeredUser = userRepository.findRegisteredUserByToken(token);
            if (registeredUser == null) {
                String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be created!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
        }

        // Check if gameType equals ranked and the request is form an unregistered user
        if (gameType == GameType.RANKED && registeredUser == null) {
            String errorMessage = "An guest user can not create a ranked lobby. Therefore, the lobby could not be created!";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
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

        checkStringConfigNullOrEmpty(token, TOKEN, ACCESSED);

        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        checkUserIsInLobby(lobby, token, ACCESSED);

        return lobby;
    }

    public byte[] getQRCodeFromLobby(String token, Long lobbyId) {
        checkStringConfigNullOrEmpty(token, TOKEN, ACCESSED);

        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        checkUserIsInLobby(lobby, token, ACCESSED);

        try {
            return lobby.getQrCode();
        }
        catch (RestClientException e) {
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
        catch (RegisteredUserLobbyNameChangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + e.getIdRegisteredPlayer() + " is a registered player, " +
                    "and thus can not change his name in the lobby!");
        }
    }

    public void updateLobby(@NotNull ILobby lobby, String token, String lobbyName, Visibility visibility, GameMode gameMode, GameType gameType) {
        checkStringConfigNullOrEmpty(token, TOKEN, UPDATED);
        if (!token.equals(lobby.getHost().getToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the host of the lobby.");
        }

        checkStringConfigNullOrEmpty(lobbyName, "name", UPDATED);
        checkEnumConfigNull(visibility, "visibility", UPDATED);
        checkEnumConfigNull(gameMode, "game mode", UPDATED);
        checkEnumConfigNull(gameType, "game type", UPDATED);

        // Check if lobby name already exists
        if (lobbyManager.getLobbyWithName(lobbyName) != null && !lobbyManager.getLobbyWithName(lobbyName).equals(lobby)) {
            String errorMessage = "The lobby name provided is not unique. Therefore, the lobby could not be updated!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        // Check if lobby type is allowed
        if (gameType != lobby.getGameType() && gameType == GameType.RANKED) {
            for (IPlayer player : lobby) {
                if (player.getRegisteredUser() == null) {
                    String errorMessage = "GameType RANKED can only be played by registered users, " +
                            "but there are unregistered users in the lobby. " +
                            "Therefore, the lobby could not be updated!";
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
                }
            }
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

    public List<Long> checkPlayersInLobby(@NotNull ILobby lobby) {
        int numberPlayers = lobby.getNumberOfPlayers();
        if (lobby.getLobbyCapacity() < numberPlayers) {
            List<Long> removedPlayerList = lobby.reducePlayersInLobby();
            lobby.setAllPlayersNotReady();
            lobby.balanceTeams();
            return removedPlayerList;
        }
        else {
            return null;
        }
    }

    public void removePlayerFromLobby(String token, Long lobbyId) {

        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);
        checkUserIsInLobby(lobby, token, "modified");

        IPlayer removedPlayer = lobby.removePlayer(token);

        // if there are not more players, remove the lobby
        if (lobby.getNumberOfPlayers() == 0) {
            lobbyManager.removeLobbyWithId(lobbyId);
        }
        // otherwise, check if the player was the host and in that case, assign a new one
        else {
            if (lobby.getHost().getId() == removedPlayer.getId()) {
                lobby.assignNewHost();
            }
        }
    }

    /**
     * Create a game for a lobby
     *
     * @param token   token of the user that tries to start a game
     * @param lobbyId id of the lobby for which a game should be created
     * @throws ResponseStatusException with HttpStatus.BadRequest If the user tried to start a game with players that are not ready
     * @throws ResponseStatusException with HttpStatus.BadRequest If the user tried to start a ranked game with players that are not registered
     * @throws ResponseStatusException with HttpStatus.BadRequest If the user tried to start a game in a not complete lobby
     * @throws ResponseStatusException with HttpStatus.Unauthorized If the user did not provide authentication
     * @throws ResponseStatusException with HttpStatus.Forbidden If the user is not the host of the lobby
     * @throws ResponseStatusException with HttpStatus.NotFound If the lobby with lobbyId does not exist
     * @throws ResponseStatusException with HttpStatus.Conflict If there is already a game for the lobby with lobbyId
     */
    public Game createGame(String token, long lobbyId) {
        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        checkStringConfigNullOrEmpty(token, TOKEN, UPDATED);

        // Check that the user is the host
        if (!lobby.getHost().getToken().equals(token)) {
            String errorMessage = "The provided authentication was incorrect. Therefore, the game could not be created!";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        // Check if a game is already running
        if (lobby.getGame() != null) {
            String errorMessage = "There exists already a game for this lobby. Therefore, the game could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        // Check that lobby is complete
        if (lobby.getNumberOfPlayers() != lobby.getGameMode().getMaxNumbersOfPlayers()) {
            String errorMessage = "The lobby is not complete. Therefore, the game could not be created!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // Check for all members that
        for (IPlayer player : lobby) {
            // player is ready
            if (!player.isReady()) {
                String errorMessage = "Not all players are ready in the lobby. Therefore, the game could not be created!";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
            // player is the correct type (registered for ranked game)
            if (lobby.getGameType() == GameType.RANKED && player.getRegisteredUser() == null) {
                String errorMessage = "Not all players are registered in the lobby. Therefore, the game could not be created!";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        }

        lobby.startGame();

        return lobby.getGame();
    }

    private void checkStringConfigNullOrEmpty(String s, @NotNull String fieldName, String errorMessageEnding) {
        if (fieldName.equals(TOKEN)) {
            if (s == null || s.isEmpty()) {
                String errorMessage = "The user needs to provide authentication to retrieve lobby information."
                        + "Therefore, the lobby could not be " + errorMessageEnding + "!";
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
            }
        }
        else {
            if (s == null || s.trim().isEmpty()) {
                String errorMessage = "The lobby " + fieldName + " provided is empty."
                        + "Therefore, the lobby could not be " + errorMessageEnding + "!";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        }
    }

    private void checkUserIsInLobby(@NotNull ILobby lobby, String token, String errorMessageEnding) {
        // Check if user is in lobby
        for (IPlayer player : lobby) {
            // If tokens match return true
            if (player.getToken().equals(token)) {
                return;
            }
        }
        String errorMessage = "The provided authentication was incorrect. Therefore, the lobby could not be " + errorMessageEnding + "!";
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }

    private <T extends Enum<T>> void checkEnumConfigNull(T config, String configName, String errorMessageEnding) {
        if (config == null) {
            String errorMessage = "The " + configName + " provided is empty. Therefore, the lobby could not be " + errorMessageEnding + "!";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    private @NotNull ILobby getLobbyByIdElseThrowNotFound(long lobbyId) {
        ILobby lobby = lobbyManager.getLobbyWithId(lobbyId);
        // Check if lobby exists else throw an error
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The lobby with the id %d was not found", lobbyId));
        }
        return lobby;
    }

    public Collection<ILobby> getLobbiesCollection() {
        return lobbyManager.getLobbiesCollection();
    }

    public LobbyDelta addPlayer(String invitationCode, Long lobbyId, String token) {
        ILobby lobby = getLobbyByIdElseThrowNotFound(lobbyId);

        if (invitationCode != null && !lobby.getInvitationCode().equals(invitationCode)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("The code %s does not match the lobby", invitationCode));
        }

        RegisteredUser registeredUser = null;
        // Check if token is set, then find user with token and link him to the host player
        if (token != null && !token.isEmpty()) {
            registeredUser = userRepository.findRegisteredUserByToken(token);
            if (registeredUser == null) {
                String errorMessage = "The provided authentication was incorrect. Therefore, you could not join the lobby!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
            }
        }

        // Check if gameType equals ranked and the request is form an unregistered user
        if (lobby.getGameType() == GameType.RANKED && registeredUser == null) {
            String errorMessage = "A guest user can not join a ranked lobby. Therefore, you could not join the lobby!";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }

        IPlayer newPlayer = lobby.generatePlayer();

        // Link the newPlayer to the registered user
        if (registeredUser != null) {
            newPlayer.linkRegisteredUser(registeredUser);
        }

        IPlayer playerWithNameChanged;

        try {
            playerWithNameChanged = lobby.addPlayer(newPlayer);
        }
        catch (FullLobbyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This lobby is already full!");
        }
        catch (LobbyNameConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The player name " + e.getConflictingName() + " is already taken!");
        }

        return new LobbyDelta(newPlayer, playerWithNameChanged);
    }

}
