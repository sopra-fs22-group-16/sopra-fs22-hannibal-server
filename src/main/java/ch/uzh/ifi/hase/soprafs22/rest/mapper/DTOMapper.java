package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerGetDTO;
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

    public static DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "ready", target = "ready")
    @Mapping(source = "team", target = "team")
    public abstract PlayerGetDTO convertPlayerToPlayerGetDTO(Player player);

    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby){
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setId(lobby.getId());
        lobbyGetDTO.setName(lobby.getName());
        lobbyGetDTO.setOwnerId(lobby.getOwner().getId());

        // Set the member list by creating playerGetDTOs
        // and storing them in the list
        LinkedList<PlayerGetDTO> members = new LinkedList<>();
        for (Player player : lobby) {
            members.add(convertPlayerToPlayerGetDTO(player));
        }
        lobbyGetDTO.setPlayers(members);

        lobbyGetDTO.setVisibility(lobby.getVisibility());
        lobbyGetDTO.setGameMode(lobby.getGameMode());
        lobbyGetDTO.setGameType(lobby.getGameType());
        lobbyGetDTO.setInvitationCode(lobby.getInvitationCode());


        return lobbyGetDTO;
    }

    int convertTeamToTeamNumber(Team team){
        return team.getTeamNumber();
    }

}
