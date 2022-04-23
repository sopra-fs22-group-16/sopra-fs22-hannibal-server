package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.DuplicateUserNameInLobbyException;
import ch.uzh.ifi.hase.soprafs22.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.game.enums.Team;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.Visibility;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.utilities.InvitationCodeGenerator;

import java.util.*;

public class Lobby implements ILobby {

    private final long id;
    private String name;
    private Visibility visibility;
    private final Game game;
    private final Player host;
    private final Map<String, Player> playerMap;
    private String invitationCode;
    private byte[] qrCode;

    public Lobby(Long id, String name, Visibility visibility) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        this.game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED);
        this.playerMap = new HashMap<>();
        // Generate the host player
        this.host = generatePlayer();
        playerMap.put(host.getToken(), host);
    }

    @Override
    public byte[] getQrCode() {
        if (this.qrCode == null) {
            //TODO resolve the highly coupled design
            this.qrCode = InvitationCodeGenerator.getQr(invitationCode);
        }
        return this.qrCode;
    }

    @Override
    public void changeReadyStatus(String token) {
        Player player = playerMap.get(token);
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
    public Player removePlayer(String token) {
        return playerMap.remove(token);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.game.setGameMode(gameMode);
    }

    @Override
    public GameMode getGameMode() {
        return this.game.getGameMode();
    }

    @Override
    public void setGameType(GameType gameType) {
        this.game.setGameType(gameType);
    }

    @Override
    public GameType getGameType() {
        return this.game.getGameType();
    }

    @Override
    public String getInvitationCode() {
        if (invitationCode == null) {
            this.invitationCode = InvitationCodeGenerator.getAlphanumericIdCode(getId());
        }
        return this.invitationCode;
    }

    @Override
    public void setUserName(String token, String newName) throws DuplicateUserNameInLobbyException, PlayerNotFoundException {
        for (Player player : playerMap.values())
            if (player.getName().equals(newName))
                throw new DuplicateUserNameInLobbyException(newName);
        Player player = getPlayer(token);
        player.setName(newName);
    }

    @Override
    public void setReady(String token, Boolean ready) throws PlayerNotFoundException {
        Player player = getPlayer(token);
        player.setReady(ready);
        // Here lobby knows if all players are ready and can inform clients through web socket.
        // Sum of players that are ready:
        // long playersReady = playerMap.values().stream().filter(Player::isReady).count();
    }

    private Player getPlayer(String token) throws PlayerNotFoundException {
        Player player = playerMap.get(token);
        if (player == null) {
            throw new PlayerNotFoundException(token);
        }
        return player;
    }

    @Override
    public void startGame() {
        // How about creating a game with the stored parameters and starting it?
        // It seems easier than the game dealing
        // with updates whenever the lobby changes. (+1)
        // this.game.start();
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
    public Player getHost() {
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
        for (Player player : playerMap.values()) {
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


    // TODO: Only for testing, feel free to reimplement with corresponding story.
    // I added an Id here as a parameter because if the lobby has to be gotten from the outside,
    // then there needs to be a token already
    public void addPlayer(Player player) {
        playerMap.put(player.getToken(), player);
    }

    @Override
    public Iterator<Player> iterator() {
        return playerMap.values().iterator();
    }
}
