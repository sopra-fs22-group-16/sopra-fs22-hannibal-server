package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

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
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final UserRepository userRepository;
    private final Map<Long, Game> gameMap = new HashMap<>();

    @Autowired
    public GameService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Game getGameById(Long id) throws GameNotFoundException {
        Game game = gameMap.get(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }


    public void attack(Long id, String token, Position attacker, Position defender) {
        try {
            getGameById(id).attack(token, attacker, defender);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not player's turn", e);
        }
        catch (TileOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Tile " + e.getPosition() + " is out of range.", e);
        }
        catch (AttackOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target is out of range.", e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of the game.", e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Game is over.", e);
        }
        catch (UnitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit not found in " + e.getPosition() + ".", e);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + e.id() + " not found.", e);
        }
        catch (WrongUnitOwnerException e) {
            throw new RuntimeException(e);
        }
        catch (WrongTargetTeamException e) {
            throw new RuntimeException(e);
        }
    }

    public void move(Long id, String token, Position start, Position end) {
        try {
            getGameById(id).move(token, start, end);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not player's turn", e);
        }
        catch (TileOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Tile " + e.getPosition() + " is out of range.", e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of the game.", e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Game is over.", e);
        }
        catch (UnitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit not found in " + e.getPosition() + ".", e);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + e.id() + " not found.", e);
        }
        catch (TargetUnreachableException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Target unreachable, unit "+e.getUnit().getType().name()+" cannot move from " +e.getStart()+ " to " + e.getEnd(), e);
        }
        catch (WrongUnitOwnerException e) {
            throw new RuntimeException(e);
        }
    }

    public void unitWait(Long id, String token, Position position) {
        try {
            getGameById(id).unitWait(token, position);
        }
        catch (NotPlayersTurnException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not player's turn", e);
        }
        catch (TileOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Tile " + e.getPosition() + " is out of range.", e);
        }
        catch (NotAMemberOfGameException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of the game.", e);
        }
        catch (GameOverException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Game is over.", e);
        }
        catch (UnitNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit not found in " + e.getPosition() + ".", e);
        }
        catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + e.id() + " not found.", e);
        }
        catch (WrongUnitOwnerException e) {
            throw new RuntimeException(e);
        }
    }
}
