package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.*;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to
 * the game and maps
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

    private final UserRepository userRepository;

    private ILobbyManager lobbyManager;
    private static final String NOT_PLAYERS_TURN = "Not player's turn";
    private static final String TILE = "Tile ";
    private static final String IS_OUT_OF_RANGE = " is out of range.";
    private static final String NOT_A_MEMBER_OF_THE_GAME = "Not a member of the game.";
    private static final String GAME_IS_OVER = "Game is over.";
    private static final String UNIT_NOT_FOUND_IN = "Unit not found in ";
    private static final String NOT_FOUND = " not found.";
    private static final String GAME_WITH_ID = "Game with id ";
    private static final String UNIT = "Unit ";
    private static final String DOES_NOT_BELONG_TO_THE_PLAYER = " does not belong to the player.";

    @Autowired
    public GameService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.lobbyManager = LobbyManager.getInstance();
    }

    private @NotNull Game getGameById(Long id) throws GameNotFoundException {
        Game game = lobbyManager.getLobbyWithId(id).getGame();
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }

    /**
     * Returns the list of units whose health got affected by the attack.
     */
    public GameDelta unitAttack(Long id, String token, @NotNull AttackCommand attackCommand) {
        try {
            Position attacker = attackCommand.getAttacker();
            Position attackerDestination = attackCommand.getAttackerDestination();
            Position defender = attackCommand.getDefender();

            Game game = getGameById(id);
            GameDelta gameDelta = game.unitAttack(token, attacker, attackerDestination, defender);
            // If gameOver info is set (game has ended) and if it is a ranked game
            // update the registered players
            if(game.getGameType() == GameType.RANKED && gameDelta.getGameOverInfo() != null){
                for(PlayerDecorator playerDecorator: game.getDecoratedPlayers().values()){
                    userRepository.save(playerDecorator.getRegisteredUser());
                }
                userRepository.flush();
            }
            return gameDelta;
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_PLAYERS_TURN, e);
        }
        catch (TileOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, TILE + e.getPosition() + IS_OUT_OF_RANGE, e);
        }
        catch (AttackOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target is out of range.", e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_A_MEMBER_OF_THE_GAME, e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, GAME_IS_OVER, e);
        }
        catch (UnitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIT_NOT_FOUND_IN + e.getPosition() + ".", e);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GAME_WITH_ID + e.id() + NOT_FOUND, e);
        }
        catch (WrongUnitOwnerException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIT + e.getUnit() + DOES_NOT_BELONG_TO_THE_PLAYER, e);
        }
        catch (WrongTargetTeamException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIT + e.getSecond() + " is not a valid target of unit " + e.getFirst() + ".", e);
        }
        catch (TargetUnreachableException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Target unreachable, unit " + e.getUnit().getType().name() + " cannot move from " + e.getStart() + " to " + e.getEnd(), e);
        }
    }

    public GameDelta unitMove(Long id, String token, @NotNull MoveCommand moveCommand) {
        try {
            Position start = moveCommand.getStart();
            Position destination = moveCommand.getDestination();
            return getGameById(id).unitMove(token, start, destination);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_PLAYERS_TURN, e);
        }
        catch (TileOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, TILE + e.getPosition() + IS_OUT_OF_RANGE, e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_A_MEMBER_OF_THE_GAME, e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, GAME_IS_OVER, e);
        }
        catch (UnitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIT_NOT_FOUND_IN + e.getPosition() + ".", e);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GAME_WITH_ID + e.id() + NOT_FOUND, e);
        }
        catch (TargetUnreachableException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Target unreachable, unit " + e.getUnit().getType().name() + " cannot move from " + e.getStart() + " to " + e.getEnd(), e);
        }
        catch (WrongUnitOwnerException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, UNIT + e.getUnit() + DOES_NOT_BELONG_TO_THE_PLAYER, e);
        }
    }

    public GameDelta surrender(Long id, String token) {
        try {
            Game game = getGameById(id);

            GameDelta gameDelta = game.surrender(token);
            // If gameOver info is set (game has ended) and if it is a ranked game
            // update the registered players
            if(game.getGameType() == GameType.RANKED && gameDelta.getGameOverInfo() != null){
                for(PlayerDecorator playerDecorator: game.getDecoratedPlayers().values()){
                    userRepository.save(playerDecorator.getRegisteredUser());
                }
                userRepository.flush();
            }
            return gameDelta;
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GAME_WITH_ID + e.id() + NOT_FOUND, e);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_PLAYERS_TURN, e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, GAME_IS_OVER, e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_A_MEMBER_OF_THE_GAME, e);
        }
    }

    // Only for testing
    public ILobbyManager setLobbyManager(ILobbyManager lobbyManager) {
        ILobbyManager temp = this.lobbyManager;
        this.lobbyManager = lobbyManager;
        return temp;
    }

    public IGameStatistics getGameStats(Long id, String token) {
        try {
            return getGameById(id).getStatistics();
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GAME_WITH_ID + e.id() + NOT_FOUND, e);
        }
    }
}
