package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;

import java.util.List;

/**
 * Delta to be sent through websocket about the game so client can process info easier.
 */
public class GameDeltaSockDTO {
    private MovementDTO move;

    private TurnInfo turnInfo;
    private List<HealthDTO> health;


    public MovementDTO getMove() {
        return move;
    }

    public void setMove(MovementDTO move) {
        this.move = move;
    }

    public TurnInfo getTurnInfo() {
        return turnInfo;
    }

    public void setTurnInfo(TurnInfo turnInfo) {
        this.turnInfo = turnInfo;
    }

    public List<HealthDTO> getHealth() {
        return health;
    }

    public void setHealth(List<HealthDTO> health) {
        this.health = health;
    }
}
