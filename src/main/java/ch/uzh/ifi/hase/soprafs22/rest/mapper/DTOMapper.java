package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerWithTokenGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.LinkedList;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public abstract class DTOMapper {

    public static final DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "ready", target = "ready")
    @Mapping(source = "team", target = "team")
    public abstract PlayerGetDTO convertIPlayerToPlayerGetDTO(IPlayer player);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "ready", target = "ready")
    @Mapping(source = "team", target = "team")
    @Mapping(source = "token", target = "token")
    public abstract PlayerWithTokenGetDTO convertIPlayerToPlayerWithTokenGetDTO(IPlayer player);

    //add invitation code if token matches
    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby, String token) {
        LobbyGetDTO lobbyGetDTO = convertILobbyToLobbyGetDTO(lobby);
        if (lobby.getHost().getToken().equals(token)) {
            lobbyGetDTO.setInvitationCode(lobby.getInvitationCode());
        }
        return lobbyGetDTO;
    }

    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby){
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setId(lobby.getId());
        lobbyGetDTO.setName(lobby.getName());
        lobbyGetDTO.setHostId(lobby.getHost().getId());

        // Set the member list by creating playerGetDTOs
        // and storing them in the list
        LinkedList<PlayerGetDTO> members = new LinkedList<>();
        for (IPlayer player : lobby) {
            members.add(convertIPlayerToPlayerGetDTO(player));
        }
        lobbyGetDTO.setPlayers(members);

        lobbyGetDTO.setVisibility(lobby.getVisibility());
        lobbyGetDTO.setGameMode(lobby.getGameMode());
        lobbyGetDTO.setGameType(lobby.getGameType());
        return lobbyGetDTO;
    }

    int convertTeamToTeamNumber(Team team){
        return team.getTeamNumber();
    }
    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "gameMode", target = "gameMode")
    @Mapping(source = "gameMap", target = "gameMap")
    public abstract GameGetDTO convertGameToGameGetDTO(Game game);
}
