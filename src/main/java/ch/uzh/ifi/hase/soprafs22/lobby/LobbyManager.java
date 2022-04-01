package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

import java.util.*;
import java.util.stream.LongStream;

public class LobbyManager implements ILobbyManager {

    private volatile static LobbyManager uniqueInstance;

    private final HashMap<Long, ILobby> lobbyList;

    private LobbyManager() {
        this.lobbyList = new HashMap<>();
    }

    public static LobbyManager getInstance() {
        if (uniqueInstance == null) {
            synchronized (LobbyManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LobbyManager();
                }
            }
        }
        return uniqueInstance;
    }

    @Override
    public ILobby removeLobbyWithId(long id) {
        return this.lobbyList.remove(id);
    }

    @Override
    public ILobby getLobbyWithId(long id) {
        return lobbyList.get(id);
    }

    @Override
    public ILobby getLobbyWithName(String name) {
        for(ILobby lobby: lobbyList.values()){
            if(lobby.getName().equals(name)) return lobby;
        }
        return null;
    }

    @Override
    public ILobby createLobby(String lobbyName, LobbyMode lobbyMode, IUser host) throws SmallestLobbyIdNotCreatable {

        long id = generateSmallestUniqueId();

        ILobby lobby = new Lobby(id, lobbyName, lobbyMode, host);
        this.lobbyList.put(id, lobby);

        return lobby;
    }

    /**
     * Generates the smallest id that is not yet in use [0,Long.MAX_VALUE]
     * @return smallest id not yet in use
     * @throws SmallestLobbyIdNotCreatable if we could not generate a unique id
     */
    private long generateSmallestUniqueId() throws SmallestLobbyIdNotCreatable {
        // Create smallest unique id

        // Get all ids and store them additionally save the largest id
        List<Long> idList = new LinkedList<>();
        long maxId = -1;
        for (ILobby lobby : lobbyList.values()) {
            idList.add(lobby.getId());
            if (maxId < lobby.getId()) maxId = lobby.getId();
        }

        // Check that we do not increase the upper range above Long.MAX_VALUE
        long lowerRange = 0;
        long upperRange;
        if (maxId < Long.MAX_VALUE - 2L) {
            upperRange = maxId + 2L;
        }
        else {
            upperRange = Long.MAX_VALUE;
        }

        // Generate a stream of longs from 0 to (exclusive) maxId+2
        // Remove all elements that already exist
        // Get smallest from them
        OptionalLong optionalId = LongStream.range(lowerRange, upperRange).filter(value -> (!idList.contains(value))).min();
        return optionalId.orElseThrow(SmallestLobbyIdNotCreatable::new);
    }

    @Override
    public Iterator<ILobby> iterator() {
        return lobbyList.values().iterator();
    }

    @Override
    public void clear(){
        lobbyList.clear();
    }
}
