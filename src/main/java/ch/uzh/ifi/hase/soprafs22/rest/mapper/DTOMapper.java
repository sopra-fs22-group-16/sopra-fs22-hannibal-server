package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.GameDelta;
import ch.uzh.ifi.hase.soprafs22.game.Position;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.game.player.PlayerDecorator;
import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.AttackCommand;
import ch.uzh.ifi.hase.soprafs22.game.units.commands.MoveCommand;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.UnitAttackPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitMoveDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket.GameDeltaWebSocketDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UnitHealthDTO;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
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
    @Mapping(source = "registeredUser", target="registered")
    public abstract PlayerGetDTO convertIPlayerToPlayerGetDTO(IPlayer player);

    protected int convertTeamToTeamNumber(Team team) {
        return team.ordinal();
    }

    protected boolean convertRegisteredUserToBooleanRegistered(RegisteredUser registeredUser){
        return registeredUser != null;
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "ready", target = "ready")
    @Mapping(source = "team", target = "team")
    @Mapping(source = "token", target = "token")
    public abstract PlayerWithTokenGetDTO convertIPlayerToPlayerWithTokenGetDTO(IPlayer player);

    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby, String token) {
        LobbyGetDTO lobbyGetDTO = convertILobbyToLobbyGetDTO(lobby);
        if (lobby.getHost().getToken().equals(token)) {
            lobbyGetDTO.setInvitationCode(lobby.getInvitationCode());
        }
        return lobbyGetDTO;
    }

    public LobbyGetDTO convertILobbyToLobbyGetDTO(ILobby lobby) {
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
        lobbyGetDTO.setGameRunning(lobby.getGame() != null && !lobby.getGame().hasEnded() );
        return lobbyGetDTO;
    }

    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "gameMode", target = "gameMode")
    @Mapping(source = "gameMap", target = "gameMap")
    @Mapping(source = "decoratedPlayers", target = "units")
    @Mapping(source = "turnNumber", target = "turnNumber")
    @Mapping(source = "playerIdCurrentTurn", target = "playerIdCurrentTurn")
    @Mapping(source = "decoratedPlayers", target = "players")
    public abstract GameGetDTO convertGameToGameGetDTO(Game game);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "health", target = "health")
    @Mapping(source = "maxHealth", target = "maxHealth")
    @Mapping(source = "defense", target = "defense")
    @Mapping(source = "attackDamage", target = "attackDamage")
    @Mapping(source = "attackRange", target = "attackRange")
    @Mapping(source = "movementRange", target = "movementRange")
    @Mapping(source = "commands", target = "commands")
    @Mapping(source = "teamId", target = "teamId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "position", target = "position")
    @Mapping(source = "moved", target = "moved")
    public abstract UnitGetDTO convertUnitToUnitGetDTO(Unit unit);

    protected List<UnitGetDTO> convertPlayerMapToUnitGetDTO(Map<String, PlayerDecorator> playerMap) {
        List<UnitGetDTO> units = new ArrayList<>();
        for (PlayerDecorator pd : playerMap.values()) {
            for (Unit u : pd.getUnits()) {
                units.add(convertUnitToUnitGetDTO(u));
            }
        }
        return units;
    }

    @Mapping(source = "x", target = "x")
    @Mapping(source = "y", target = "y")
    public abstract PositionDTO convertPositionToPositionDTO(Position position);

    protected Position convertPositionDTOToPosition(PositionDTO position) {
        return new Position(position.getX(), position.getY());
    }

    protected Map<Long, PlayerGetDTO> convertTokenPlayerMapToIdPlayerGetDTOMap(Map<String, PlayerDecorator> playerMap) {
        Map<Long, PlayerGetDTO> newPlayerMap = new HashMap<>();
        for (IPlayer player : playerMap.values()) {
            newPlayerMap.put(player.getId(), convertIPlayerToPlayerGetDTO(player));
        }
        return newPlayerMap;
    }

    @Mapping(source = "attacker", target = "attacker")
    @Mapping(source = "defender", target = "defender")
    @Mapping(source = "attackerDestination", target = "attackerDestination")
    public AttackCommand convertUnitAttackPutDTOToAttackCommand(UnitAttackPutDTO unitAttackPutDTO) {
        Position attacker = convertPositionDTOToPosition(unitAttackPutDTO.getAttacker());
        Position defender = convertPositionDTOToPosition(unitAttackPutDTO.getDefender());
        Position attackerDestination = convertPositionDTOToPosition(unitAttackPutDTO.getAttackerDestination());
        return new AttackCommand(attacker, defender, attackerDestination);
    }

    public MoveCommand convertUnitMovePutDTOToMoveCommand(UnitMoveDTO unitMoveDTO) {
        Position start = convertPositionDTOToPosition(unitMoveDTO.getStart());
        Position destination = convertPositionDTOToPosition(unitMoveDTO.getDestination());
        return new MoveCommand(start, destination);
    }

    @Mapping(source = "moveCommand", target = "move")
    @Mapping(source = "turnInfo", target = "turnInfo")
    @Mapping(source = "unitHealths", target = "unitHealths")
    @Mapping(source = "gameOverInfo", target = "gameOverInfo")
    @Mapping(source = "surrenderInfo", target = "surrenderInfo")
    public abstract GameDeltaWebSocketDTO convertGameDeltaToGameDeltaWebSocketDTO(GameDelta gameDelta);

    protected List<UnitHealthDTO> convertUnitHealthsMapToUnitHealthsList(Map<Position, Integer> unitHealthsMap) {
        if (unitHealthsMap == null)
            return null;
        List<UnitHealthDTO> unitHealthsList = new ArrayList<>();
        for (var uh : unitHealthsMap.entrySet()) {
            PositionDTO p = convertPositionToPositionDTO(uh.getKey());
            int health = uh.getValue();
            unitHealthsList.add(new UnitHealthDTO(p, health));
        }
        return unitHealthsList;
    }
}
