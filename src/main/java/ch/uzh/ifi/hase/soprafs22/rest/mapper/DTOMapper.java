package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.*;

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

    protected int convertTeamToTeamNumber(Team team){
        return team.ordinal();
    }
  
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

    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "gameMode", target = "gameMode")
    @Mapping(source = "gameMap", target = "gameMap")
    @Mapping(source = "playerMap", target = "units")
    @Mapping(source = "turnNumber", target = "turnNumber")
    @Mapping(source = "playerIdCurrentTurn", target = "playerIdCurrentTurn")
    @Mapping(source = "playerMap", target = "players")
    public abstract GameGetDTO convertGameToGameGetDTO(Game game);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "health", target = "health")
    @Mapping(source = "defense", target = "defense")
    @Mapping(source = "attackDamage", target = "attackDamage")
    @Mapping(source = "attackRange", target = "attackRange")
    @Mapping(source = "movementRange", target = "movementRange")
    @Mapping(source = "commands", target = "commands")
    @Mapping(source = "teamId", target = "teamId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "position", target = "position")
    public abstract UnitGetDTO convertUnitToUnitDTO(Unit unit);

    public Position convertPositionDTOToPosition(PositionDTO position){
        return new Position(position.getX(), position.getY());
    }
    protected List<UnitGetDTO> convertPlayerMapToUnitDTO(Map<String, PlayerDecorator> playerMap){
        List<UnitGetDTO> units = new ArrayList<>();
        for(PlayerDecorator pd : playerMap.values()){
            for (Unit u: pd.getUnits()){
                units.add(convertUnitToUnitDTO(u));
            }
        }
        return units;
    }

    @Mapping(source = "x", target="x")
    @Mapping(source = "y", target="y")
    public abstract PositionDTO convertPositionToPositionDTO(Position position);

    public long convertIPlayerToPlayerId(IPlayer player){
        return player.getId();
    }

    public Map<Long, PlayerGetDTO> convertPlayerDecoratorMapTokenToPlayerMapId(Map<String, PlayerDecorator> playerMap) {
        Map<Long, PlayerGetDTO> newPlayerMap = new HashMap<>();
        for(IPlayer player: playerMap.values()){
            newPlayerMap.put(player.getId(), convertIPlayerToPlayerGetDTO(player));
        }
        return newPlayerMap;
    }
}
