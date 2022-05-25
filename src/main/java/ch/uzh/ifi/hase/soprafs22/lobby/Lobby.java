package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.*;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.player.IPlayer;
import ch.uzh.ifi.hase.soprafs22.game.player.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import ch.uzh.ifi.hase.soprafs22.utilities.InvitationCodeGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Random random;

    public Lobby(Long id, String name, Visibility visibility, RegisteredUser registeredUserAsHost) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        this.playerMap = new HashMap<>();
        // Generate the host player
        this.host = generatePlayer();
        // Link the host if the registered user is set
        if (registeredUserAsHost != null) {
            host.linkRegisteredUser(registeredUserAsHost);
        }
        playerMap.put(host.getToken(), host);
        this.random = new Random();
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
    public void setGameMode(@NotNull GameMode gameMode) {
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
    public void setUserName(String token, String newName) throws DuplicateUserNameInLobbyException, PlayerNotFoundException, RegisteredUserLobbyNameChangeException {
        for (IPlayer player : playerMap.values())
            if (player.getName().equals(newName))
                throw new DuplicateUserNameInLobbyException(newName);
        IPlayer player = getPlayer(token);
        if(player.getRegisteredUser() != null){
            throw new RegisteredUserLobbyNameChangeException(player.getId());
        }
        player.setName(newName);
    }

    @Override
    public void setReady(String token, Boolean ready) throws PlayerNotFoundException {
        IPlayer player = getPlayer(token);
        player.setReady(ready);
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
    public List<Long> reducePlayersInLobby() {
        List<Long> removedPlayerIds = new LinkedList<>();
        Iterator <IPlayer> it = iterator();
        while(it.hasNext()){
            IPlayer player = it.next();
            if(player != host && lobbyCapacity < getNumberOfPlayers()){
                removedPlayerIds.add(player.getId());
                it.remove();
            }
        }
        return removedPlayerIds;
    }

    @Override
    public boolean setAllPlayersNotReady(){
        for (IPlayer player : playerMap.values()){
            player.setReady(false);
        }
        return true;
    }

    @Override
    public boolean balanceTeams(){
        Team t = null;
        // we alternate teams between consecutive players
        for (IPlayer player : playerMap.values()){
            if (t == null){
                player.setTeam(Team.RED);
            }
            else{
                if(t == Team.RED){
                    player.setTeam(Team.BLUE);
                }
                else{
                    player.setTeam(Team.RED);
                }
            }
            t = player.getTeam();
        }
        return true;
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
        setAllPlayersNotReady();
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

    @Override
    public int getLobbyCapacity() {
        return lobbyCapacity;
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

        String username = generateUniqueNameInLobby();

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

    private String generateUniqueNameInLobby(){
        String playerName = "Player-";
        int playerNumber = 0;
        boolean uniqueNameFound = false;

        String newName = "";
        while (!uniqueNameFound) {
            newName = playerName + playerNumber;

            uniqueNameFound = true;
            for(IPlayer player : playerMap.values()){
               if(player.getName().equals(newName)){
                   uniqueNameFound = false;
                   ++playerNumber;
                   break;
               }
            }
        }
        return newName;
    }

    /**
     * Add a player to the lobby
     * @param player the player which should be added to the lobby
     * @return  null, if there was no name conflict
     *          IPlayer, if there was a name conflict, the returned object is the player of which the name was changed
     * @throws LobbyNameConflictException If there were multiple name conflicts, or a nameConflict between two registered players
     * @throws FullLobbyException If the lobby is already full
     */
    @Override
    public @Nullable IPlayer addPlayer(IPlayer player) throws FullLobbyException, LobbyNameConflictException {
        IPlayer playerWithNameConflict = null;
        for(IPlayer lobbyPlayer : playerMap.values()){
            // Check if there is a name conflict
            if(lobbyPlayer.getName().equals(player.getName())){

                // Check if there was already a name conflict
                if(playerWithNameConflict != null) throw new LobbyNameConflictException("Multiple name conflicts with name " + lobbyPlayer.getName(), lobbyPlayer.getName());

                // Check if user with conflict is a registered user.
                if(lobbyPlayer.getRegisteredUser() != null) throw new LobbyRegisteredUsersNameConflictException("Name conflict with registered user with name: " + lobbyPlayer.getName(), lobbyPlayer.getName());

                playerWithNameConflict = lobbyPlayer;
            }
        }

        if (playerMap.size() >= lobbyCapacity) {
            throw new FullLobbyException();
        }

        playerMap.put(player.getToken(), player);

        if(playerWithNameConflict != null)
            playerWithNameConflict.setName(generateUniqueNameInLobby());

        return playerWithNameConflict;
    }

    @Override
    public Iterator<IPlayer> iterator() {
        return playerMap.values().iterator();
    }
}
