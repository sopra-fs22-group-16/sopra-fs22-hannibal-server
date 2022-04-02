package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobbyManager;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyPutDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class LobbyManager implements ILobbyManager {
    private final Map<Long, ILobby> lobbies = new HashMap<>();
    private final Set<String> lobbyNames = new HashSet<>();

    @Override
    public void addLobby(ILobby lobby) {
        lobbies.put(lobby.getId(), lobby);
        lobbyNames.add(lobby.getName());
    }

    @Override
    public ILobby removeLobby(long lobbyId) {
        ILobby lobby = lobbies.remove(lobbyId);
        lobbyNames.remove(lobby.getName());
        return lobby;
    }

    @Override
    public List<ILobby> getLobbyList() {
        return new ArrayList<>(lobbies.values());
    }

    @Override
    public void updateLobby(long userId, long lobbyId, LobbyPutDTO lobbyPutDTO) {
        ILobby lobby = getLobbyById(lobbyId);
        if (lobby.getHost().getId() != userId)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not the host of the lobby.");
        // Not null, value was provided by DTO.
        if (lobbyPutDTO.getVisibility() != null) {
            switch(lobbyPutDTO.getVisibility()) {
                case "PUBLIC":
                    lobby.setLobbyMode(LobbyMode.PUBLIC);
                    break;
                case "PRIVATE":
                    lobby.setLobbyMode(LobbyMode.PRIVATE);
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

    @Override
    public ILobby getLobbyById(long lobbyId) {
        return this.lobbies.get(lobbyId);
    }
}
