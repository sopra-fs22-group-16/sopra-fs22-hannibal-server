package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.HealthPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitMovePutDTO;

import java.util.List;

/**
 * Delta to be sent through web socket about the game so client can process info easier.
 */
public class GameDeltaWebSocketDTO {
    private UnitMovePutDTO move;

    private TurnInfo turnInfo;
    private List<HealthPutDTO> health;


    public UnitMovePutDTO getMove() {
        return move;
    }

    public void setMove(UnitMovePutDTO move) {
        this.move = move;
    }

    public TurnInfo getTurnInfo() {
        return turnInfo;
    }

    public void setTurnInfo(TurnInfo turnInfo) {
        this.turnInfo = turnInfo;
    }

    public List<HealthPutDTO> getHealth() {
        return health;
    }

    public void setHealth(List<HealthPutDTO> health) {
        this.health = health;
    }
}
