package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;

/**
 * Keeps track of turns. Both the turn number of turn it is.
 */
public class Turn {

    private final List<PlayerDecorator> turnOrder;
    private int turnNumber = 0;
    private int turnIndex = 0;

    public Turn(@NotNull Collection<PlayerDecorator> players) {
        List<PlayerDecorator> redPlayers = players.stream().filter(player -> player.getTeam() == Team.RED).collect(Collectors.toList());
        List<PlayerDecorator> bluePlayers = players.stream().filter(player -> player.getTeam() == Team.BLUE).collect(Collectors.toList());
        List<PlayerDecorator> turnOrder = new ArrayList<>();
        for (int i = 0; i < min(redPlayers.size(), bluePlayers.size()); i++) {
            turnOrder.add(redPlayers.get(i));
            turnOrder.add(bluePlayers.get(i));
        }
        this.turnOrder = turnOrder;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public TurnInfo nextTurn() {
        turnNumber++;
        do {
            // take module to wrap around the list.
            turnIndex = ++turnIndex % turnOrder.size();
        }while(turnOrder.get(turnIndex).getUnits().size() < 1);
        return getTurnInfo();
    }

    public TurnInfo getTurnInfo() {
        return new TurnInfo(turnNumber, getPlayerId());
    }

    public long getPlayerId() {
        return turnOrder.get(turnIndex).getId();
    }
}
