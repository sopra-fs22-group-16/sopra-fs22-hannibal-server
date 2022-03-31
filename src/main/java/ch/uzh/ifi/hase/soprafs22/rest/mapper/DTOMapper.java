package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.lobby.Lobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.MemberGetDTO;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;
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

    public LobbyGetDTO convertLobbyToLobbyGetDTO(Lobby lobby){
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        lobbyGetDTO.setLobbyId(lobby.getId());
        lobbyGetDTO.setName(lobby.getName());
        lobbyGetDTO.setOwner(lobby.getHost().getId());

        // Set the member list by creating memberGetDTOs
        // and storing them in the list
        LinkedList<MemberGetDTO> members = new LinkedList<>();
        for (Iterator<IUser> it = lobby.iterator(); it.hasNext(); ) {
            IUser user = it.next();
            MemberGetDTO userGetDTO = new MemberGetDTO();
            userGetDTO.setId(user.getId());
            userGetDTO.setName(user.getUsername());
            // userGetDTO.setReady(lobby.isUserReady(user));
            members.add(userGetDTO);
        }
        lobbyGetDTO.setMembers(members);

        lobbyGetDTO.setVisibility(lobby.getLobbyMode());

        // lobbyGetDTO.setGameMode(lobby.getGame().getGameMode());
        // lobbyGetDTO.setRanked(lobby.getGame().getGameType());
        // lobbyGetDTO.setInvitationCode(lobby.getInvitationCode());


        return lobbyGetDTO;
    }

}
