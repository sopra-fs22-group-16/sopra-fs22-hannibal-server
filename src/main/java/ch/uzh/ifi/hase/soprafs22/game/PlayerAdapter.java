package ch.uzh.ifi.hase.soprafs22.game;

import ch.uzh.ifi.hase.soprafs22.game.enums.Team;

import java.util.HashMap;
import java.util.Map;

public class PlayerAdapter {
    private final Player player;
    private final Map<Long, IUnit> units;

    public PlayerAdapter(Player player) {
        this.player = player;
        this.units = new HashMap<>();
    }

    public long getId() {
        return player.getId();
    }

    public String getToken() {
        return player.getToken();
    }

    public Team getTeam() {
        return player.getTeam();
    }

    public IUnit getUnitById(long id){
        return units.get(id);
    }

}
