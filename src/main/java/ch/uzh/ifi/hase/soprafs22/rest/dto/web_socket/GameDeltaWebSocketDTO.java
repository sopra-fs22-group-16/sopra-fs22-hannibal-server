package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.game.TurnInfo;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitMoveDTO;

import java.util.List;
import java.util.Map;

/**
 * Delta to be sent through web socket about the game so client can process info easier.
 */
public class GameDeltaWebSocketDTO {
    private UnitMoveDTO move;
    private List<UnitHealthsWebSocketDTO> unitHealths;
    private TurnInfo turnInfo;

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

    public List<UnitHealthsWebSocketDTO> getUnitHealths() {
        return unitHealths;
    }

    public void setUnitHealths(List<UnitHealthsWebSocketDTO> unitHealths) {
        this.unitHealths = unitHealths;
    }
}
