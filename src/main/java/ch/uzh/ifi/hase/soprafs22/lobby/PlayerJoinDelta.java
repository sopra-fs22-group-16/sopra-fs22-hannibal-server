package ch.uzh.ifi.hase.soprafs22.lobby;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;

public class PlayerJoinDelta {
    private final IPlayer newPlayer;
    private final IPlayer playerWithChangedName;

    public PlayerJoinDelta(IPlayer newPlayer,IPlayer playerWithChangedName) {
        this.newPlayer = newPlayer;
        this.playerWithChangedName =  playerWithChangedName;
    }

    public IPlayer getNewPlayer() {
        return newPlayer;
    }

    public IPlayer getPlayerWithChangedName() {
        return playerWithChangedName;
    }
}
