package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.LongStream;

import java.util.*;

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
    public ILobby createLobby(String lobbyName, Visibility visibility) throws SmallestIdNotCreatable {

        long id = generateSmallestUniqueId();

        ILobby lobby = new Lobby(id, lobbyName, visibility);
        this.lobbyList.put(id, lobby);

        return lobby;
    }

    /**
     * Generates the smallest id that is not yet in use [0,Long.MAX_VALUE]
     * @return smallest id not yet in use
     * @throws SmallestIdNotCreatable if we could not generate a unique id
     */
    private long generateSmallestUniqueId() throws SmallestIdNotCreatable {
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
        return optionalId.orElseThrow(SmallestIdNotCreatable::new);
    }

    @Override
    public Iterator<ILobby> iterator() {
        return lobbyList.values().iterator();
    }

    @Override
    public void clear(){
        lobbyList.clear();
    }

    @Override
    public void updateLobby(long userId, long lobbyId, LobbyPutDTO lobbyPutDTO) {
        ILobby lobby = getLobbyWithId(lobbyId);
        if (lobby.getOwner().getId() != userId)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not the host of the lobby.");
        // Not null, value was provided by DTO.
        if (lobbyPutDTO.getVisibility() != null) {
            switch(lobbyPutDTO.getVisibility()) {
                case "PUBLIC":
                    lobby.setLobbyMode(Visibility.PUBLIC);
                    break;
                case "PRIVATE":
                    lobby.setLobbyMode(Visibility.PRIVATE);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visibility cannot be:" + lobbyPutDTO.getVisibility());
            }
        }
        if (lobbyPutDTO.getGameType() != null){
            switch(lobbyPutDTO.getGameType()) {
                case "RANKED":
                    lobby.setGameType(GameType.RANKED);
                    break;
                case "UNRANKED":
                    lobby.setGameType(GameType.UNRANKED);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GameType cannot be:" + lobbyPutDTO.getGameType());
            }
        }
        if (lobbyPutDTO.getGameMode() !=null){
            switch (lobbyPutDTO.getGameMode()) {
                case "ONE_VS_ONE":
                    lobby.setGameMode(GameMode.ONE_VS_ONE);
                    break;
                case "TWO_VS_TWO":
                    lobby.setGameMode(GameMode.TWO_VS_TWO);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mode cannot be:" + lobbyPutDTO.getGameMode());
            }
        }
        if (lobbyPutDTO.getName() !=null){
            //if the lobby name is empty(spaces)
            if (lobbyPutDTO.getName().replaceAll("\\s+","").length() == 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby name should not be empty.");
            if (lobbyNames.contains(lobbyPutDTO.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Lobby Name " + lobbyPutDTO.getName() + " already taken.");
            }
            lobbyNames.remove(lobby.getName());
            lobbyNames.add(lobbyPutDTO.getName());
            lobby.setName(lobbyPutDTO.getName());
        }
    }
}
