package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

import java.util.List;
import java.util.Map;

public class GameStatisticsGetDTO {
    private Map<Long, List<Integer>> unitsPerPlayer;
    private float averageUnitsPerTurn;
    private float averageKillsPerTurn;
    private int totalMoves;

    public Map<Long, List<Integer>> getUnitsPerPlayer() {
        return unitsPerPlayer;
    }

    public void setUnitsPerPlayer(Map<Long, List<Integer>> unitsPerPlayer) {
        this.unitsPerPlayer = unitsPerPlayer;
    }

    public float getAverageUnitsPerTurn() {
        return averageUnitsPerTurn;
    }

    public void setAverageUnitsPerTurn(float averageUnitsPerTurn) {
        this.averageUnitsPerTurn = averageUnitsPerTurn;
    }

    public float getAverageKillsPerTurn() {
        return averageKillsPerTurn;
    }

    public void setAverageKillsPerTurn(float averageKillsPerTurn) {
        this.averageKillsPerTurn = averageKillsPerTurn;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }
}
