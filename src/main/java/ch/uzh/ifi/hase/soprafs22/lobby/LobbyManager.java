package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatableException;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.LongStream;

import java.util.*;

public class LobbyManager implements ILobbyManager {

    private static final LobbyManager uniqueInstance = new LobbyManager();

    private final HashMap<Long, ILobby> lobbyMap;

    private LobbyManager() {
        this.lobbyMap = new HashMap<>();
    }

    public static LobbyManager getInstance() {
        return uniqueInstance;
    }

    @Override
    public ILobby removeLobbyWithId(long id) {
        return this.lobbyMap.remove(id);
    }

    @Override
    public ILobby getLobbyWithId(long id) {
        return lobbyMap.get(id);
    }

    @Override
    public ILobby getLobbyWithName(String name) {
        for (ILobby lobby : lobbyMap.values()) {
            if (lobby.getName().equals(name)) return lobby;
        }
        return null;
    }

    @Override
    public ILobby createLobby(String lobbyName, Visibility visibility) throws SmallestIdNotCreatableException {
        long id = generateSmallestUniqueId();

        ILobby lobby = new Lobby(id, lobbyName, visibility);
        this.lobbyMap.put(id, lobby);

        return lobby;
    }

    /**
     * Generates the smallest id that is not yet in use [0,Long.MAX_VALUE]
     *
     * @return smallest id not yet in use
     * @throws SmallestIdNotCreatableException if we could not generate a unique id
     */
    private long generateSmallestUniqueId() throws SmallestIdNotCreatableException {
        // Create smallest unique id

        // Get all ids and store them additionally save the largest id
        List<Long> idList = new LinkedList<>();
        long maxId = -1;
        for (ILobby lobby : lobbyMap.values()) {
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
        return optionalId.orElseThrow(() -> new SmallestIdNotCreatableException("LobbyManager could not generate smallest id - no valid id left"));
    }

    @Override
    public void clear() {
        lobbyMap.clear();
    }

    @Override
    public void modifyPlayer(String token, Long lobbyId, PlayerPutDTO playerPutDTO) {
        ILobby lobby = lobbyMap.get(lobbyId);
        String newName = playerPutDTO.getName();
        Boolean ready = playerPutDTO.getReady();
        if (newName != null) {
            if (isEmpty(newName))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username should not be empty.");
            lobby.setUserName(token, newName);
        }
        if (ready != null) {
            lobby.setReady(token, ready);
        }
    }

    @Override
    public Iterator<ILobby> iterator() {
        return lobbyMap.values().iterator();
    }

    private static boolean isEmpty(String s) {
        return s.replaceAll("\\s+","").length() == 0;
    }
}
