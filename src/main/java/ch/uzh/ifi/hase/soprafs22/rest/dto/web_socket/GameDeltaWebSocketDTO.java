package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.game.GameOverInfo;
import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitHealthDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitMoveDTO;

import java.util.List;

/**
 * Delta to be sent through web socket about the game so client can process info easier.
 */
public class GameDeltaWebSocketDTO {
    private UnitMoveDTO move;
    private List<UnitHealthDTO> unitHealths;
    private TurnInfo turnInfo;
    private GameOverInfo gameOverInfo;

    public UnitMoveDTO getMove() {
        return move;
    }

    public void setMove(UnitMoveDTO move) {
        this.move = move;
    }

    public TurnInfo getTurnInfo() {
        return turnInfo;
    }

    public void setTurnInfo(TurnInfo turnInfo) {
        this.turnInfo = turnInfo;
    }

    public List<UnitHealthDTO> getUnitHealths() {
        return unitHealths;
    }

    public void setUnitHealths(List<UnitHealthDTO> unitHealths) {
        this.unitHealths = unitHealths;
    }

    public GameOverInfo getGameOverInfo() {
        return gameOverInfo;
    }

    public void setGameOverInfo(GameOverInfo gameOverInfo) {
        this.gameOverInfo = gameOverInfo;
    }
}
