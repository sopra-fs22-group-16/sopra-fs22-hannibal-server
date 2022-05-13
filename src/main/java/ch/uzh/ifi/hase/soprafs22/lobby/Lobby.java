package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.FullLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.utilities.InvitationCodeGenerator;

import java.util.*;

public class Lobby implements ILobby {

    private final long id;
    private int lobbyCapacity;
    private String name;
    private Visibility visibility;
    private Game game;
    private IPlayer host;
    private final Map<String, IPlayer> playerMap;
    private GameMode gameMode;
    private GameType gameType;
    private String invitationCode;
    private byte[] qrCode;

    public Lobby(Long id, String name, Visibility visibility) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        this.playerMap = new HashMap<>();
        // Generate the host player
        this.host = generatePlayer();
        playerMap.put(host.getToken(), host);
    }

    @Override
    public byte[] getQrCode() {
        if (this.qrCode == null) {
            this.qrCode = InvitationCodeGenerator.getQr(invitationCode);
        }
        return qrCode;
    }

    @Override
    public void changeReadyStatus(String token) {
        IPlayer player = playerMap.get(token);
        player.setReady(!player.isReady());
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public IPlayer removePlayer(String token) {
        return playerMap.remove(token);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        this.lobbyCapacity = gameMode.getMaxNumbersOfPlayers();
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public GameType getGameType() {
        return gameType;
    }

    @Override
    public String getInvitationCode() {
        if (invitationCode == null) {
            invitationCode = InvitationCodeGenerator.getAlphanumericIdCode(this.id);
        }
        return invitationCode;
    }

    @Override
    public void setUserName(String token, String newName) throws DuplicateUserNameInLobbyException, PlayerNotFoundException {
        for (IPlayer player : playerMap.values())
            if (player.getName().equals(newName))
                throw new DuplicateUserNameInLobbyException(newName);
        IPlayer player = getPlayer(token);
        player.setName(newName);
    }

    @Override
    public void setReady(String token, Boolean ready) throws PlayerNotFoundException {
        IPlayer player = getPlayer(token);
        player.setReady(ready);
        // Here lobby knows if all players are ready and can inform clients through web socket.
        // Sum of players that are ready:
        // long playersReady = playerMap.values().stream().filter(Player::isReady).count();
        // startGame();
    }

    @Override
    public int getNumberOfPlayers() {
        return playerMap.size();
    }

    @Override
    public void assignNewHost() {
        // the first available player is assigned as host
        for (IPlayer player : playerMap.values()){
            if(player != this.host){
                this.host = player;
                break;
            }
        }
    }

    @Override
    public IPlayer getPlayer(String token) throws PlayerNotFoundException {
        IPlayer player = playerMap.get(token);
        if (player == null) {
            throw new PlayerNotFoundException(token);
        }
        return player;
    }

    @Override
    public void startGame(){
        this.game = new Game(this.gameMode, this.gameType, this.playerMap);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IPlayer getHost() {
        return host;
    }

    //TODO: This method does not belong here
    // This is currently the only way to add players to test lobbies! We need visibility for testing.
    public Player generatePlayer() {

        Map<Team, Integer> numberOfTeamMembers = new EnumMap<>(Team.class);
        for (Team t : Team.values()) {
            numberOfTeamMembers.put(t, 0);
        }

        long generatedId = 0L;
        // Get all ids currently in use
        // Count the number of players in each team
        Set<Long> idSet = new HashSet<>();
        for (IPlayer player : playerMap.values()) {
            idSet.add(player.getId());
            int teamMembers = numberOfTeamMembers.get(player.getTeam());
            numberOfTeamMembers.put(player.getTeam(), teamMembers + 1);
        }
        // if id already in use increase by 1
        while (idSet.contains(generatedId)) {
            ++generatedId;
        }

        String username = "Player-" + generatedId;

        String token = UUID.randomUUID().toString();

        // Find team with the lowest number of players
        Team team = Team.values()[0];
        for (Team t : Team.values()) {
            int teamMembers = numberOfTeamMembers.get(t);
            if (teamMembers < numberOfTeamMembers.get(team))
                team = t;
        }

        return new Player(generatedId, username, token, team);
    }


    public void addPlayer(IPlayer player) throws FullLobbyException {
        if (playerMap.size() >= lobbyCapacity) {
            throw new FullLobbyException();
        }
        playerMap.put(player.getToken(), player);
    }

    @Override
    public Iterator<IPlayer> iterator() {
        return playerMap.values().iterator();
    }
}
