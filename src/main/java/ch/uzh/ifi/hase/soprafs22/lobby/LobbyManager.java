package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.exceptions.SmallestLobbyIdNotCreateable;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.LongStream;

public class LobbyManager implements ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager {

    public volatile static LobbyManager uniqueInstance;
    private LobbyManager() {
        this.lobbyList = new LinkedList<>();
    }
    public static LobbyManager getInstance(){
        if(uniqueInstance == null){
            synchronized (LobbyManager.class){
                if(uniqueInstance == null){
                    uniqueInstance = new LobbyManager();
                }
            }
        }
        return uniqueInstance;
    }

    private List<ILobby> lobbyList;

    @Override
    public void addLobby(ILobby lobby) {
        this.lobbyList.add(lobby);
    }

    @Override
    public ILobby removeLobby(int index) {
        return this.lobbyList.remove(index);
    }

    @Override
    public List<ILobby> getLobbyList() {
        return this.lobbyList;
    }

    @Override
    public ILobby createLobby(IUser host) throws SmallestLobbyIdNotCreateable {

        long id = generateSmallestUniqueId();
        String lobbyName = "Lobby-" + id;

        ILobby lobby = new Lobby(id, lobbyName, LobbyMode.PRIVATE, host);
        this.addLobby(lobby);

        return lobby;
    }

    /**
     * Generates the smallest id that is not yet in use
     * @return smallest id not yet in use
     * @throws SmallestLobbyIdNotCreateable if we could not generate a unique id
     */
    private long generateSmallestUniqueId() throws SmallestLobbyIdNotCreateable {
        // Create smallest unique id

        // Get all ids and store them additionally save the largest id
        List<Long> idList = new LinkedList<>();
        long maxId = -1;
        for(ILobby lobby : lobbyList){
            idList.add(lobby.getId());
            if(maxId < lobby.getId()) maxId = lobby.getId();
        }

        // Check that we do not increase the upper range above Long.MAX_VALUE
        long lowerRange = 0;
        long upperRange;
        if(maxId < Long.MAX_VALUE-2L) {
            upperRange = maxId + 2L;
        }else{
            upperRange = Long.MAX_VALUE;
        }

        // Generate a stream of longs from 0 to (exclusive) maxId+2
        // Remove all elements that already exist
        // Get smallest from them
        OptionalLong optionalId = LongStream.range(lowerRange,upperRange).filter(value -> (!idList.contains(value))).min();
        return optionalId.orElseThrow(SmallestLobbyIdNotCreateable::new);
    }
}
