package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.MemberGetDTO;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
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

    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby){
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setLobbyId(lobby.getId());
        lobbyGetDTO.setName(lobby.getName());
        lobbyGetDTO.setOwner(lobby.getHost().getId());

        // Set the member list by creating memberGetDTOs
        // and storing them in the list
        LinkedList<MemberGetDTO> members = new LinkedList<>();
        for (IUser user : lobby) {
            MemberGetDTO memberGetDTO = new MemberGetDTO();
            memberGetDTO.setId(user.getId());
            memberGetDTO.setName(user.getUsername());
            // userGetDTO.setReady(lobby.isUserReady(user));
            members.add(memberGetDTO);
        }
        lobbyGetDTO.setMembers(members);

        lobbyGetDTO.setVisibility(lobby.getLobbyMode());

        lobbyGetDTO.setGameMode(lobby.getGameMode());
        lobbyGetDTO.setRanked(lobby.getGameType());
        lobbyGetDTO.setInvitationCode(lobby.getInvitationCode());


        return lobbyGetDTO;
    }

}
