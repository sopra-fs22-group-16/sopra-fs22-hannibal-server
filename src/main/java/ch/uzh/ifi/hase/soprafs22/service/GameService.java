package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.*;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.lobby.LobbyManager;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Game getGameById(Long id) throws GameNotFoundException {
        Game game = lobbyManager.getLobbyWithId(id).getGame();
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }

    /**
     * Returns the list of units whose health got affected by the attack.
     */
    public GameDelta unitAttack(Long id, String token, AttackCommand attackCommand) {
        try {
            Game game = getGameById(id);
            Position attacker = attackCommand.getAttacker();
            Position attackerDestination = attackCommand.getAttackerDestination();
            Position attackerArrival = game.unitMove(token, attacker, attackerDestination);
            Position defender = attackCommand.getDefender();
            List<Unit> units = game.unitAttack(token, attackerArrival, defender);
            MoveCommand moveCommand = new MoveCommand(attacker, attackerDestination);
            TurnInfo turnInfo = game.haveAllUnitsOfPlayerMoved(token) && game.resetUnitsFromPreviousTurn(token) ? game.nextTurn() : null;
            Map<Position, Integer> unitHealths = units.stream().collect(Collectors.toMap(Unit::getPosition, Unit::getHealth));
            GameOverInfo gameOverInfo = game.hasEnded() ? game.getGameOverInfo() : null;
            return new GameDelta(moveCommand, turnInfo, unitHealths, gameOverInfo);
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
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
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

    public GameDelta unitMove(Long id, String token, MoveCommand moveCommand) {
        try {
            Game game = getGameById(id);
            Position start = moveCommand.getStart();
            Position destination = moveCommand.getDestination();
            Position unitArrival = game.unitMove(token, start, destination);
            MoveCommand executedMove = new MoveCommand(start, unitArrival);
            TurnInfo turnInfo = game.haveAllUnitsOfPlayerMoved(token) && game.resetUnitsFromPreviousTurn(token) ? game.nextTurn() : null;
            GameOverInfo gameOverInfo = game.hasEnded() ? game.getGameOverInfo() : null;
            return new GameDelta(executedMove, turnInfo, gameOverInfo);
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
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
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

    public GameDelta playerSurrender(Long id, String token) {
        try {
            Game game = getGameById(id);
            game.playerSurrender(token);
            TurnInfo turnInfo = game.haveAllUnitsOfPlayerMoved(token) && game.resetUnitsFromPreviousTurn(token) ? game.nextTurn() : null;
            GameOverInfo gameOverInfo = game.getGameOverInfo();
            return new GameDelta(turnInfo, gameOverInfo);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GAME_WITH_ID + e.id() + NOT_FOUND, e);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_PLAYERS_TURN, e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NOT_A_MEMBER_OF_THE_GAME, e);
        }
    }

    // Only for testing
    public void setLobbyManager(ILobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }
}
