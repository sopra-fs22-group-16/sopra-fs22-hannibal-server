package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.exceptions.SmallestIdNotCreatable;
import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.Player;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.LongStream;

public class Lobby implements ILobby {

    private final long id;
    private String name;
    private LobbyMode lobbyMode;
    private Game game;
    private final Player host;
    private final List<Player> playerList;
    private String invitationCode;
    private final String HANNIBAL_URL = "https://sopra-fs22-group-16-client.herokuapp.com?=";
    private final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code";

    public Lobby(Long id, String name, LobbyMode lobbyMode) {
        this.id = id;
        this.name = name;
        this.lobbyMode = lobbyMode;
        this.game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED);
        this.playerList = new LinkedList<>();

        // Generate the host player
        this.host = generatePlayer();
        playerList.add(host);
    }

    @Override
    public byte[] generateQrCode(String code) {
        String data = URLEncoder.encode(HANNIBAL_URL + code, StandardCharsets.UTF_8);
        RestTemplate restTemplate = new RestTemplate();
        String url = QR_API_URL + "/?data=" + data + "&size=100x100";
        return restTemplate.getForObject(url, byte[].class);
    }

    @Override
    public void changeReadyStatus(int token) {

    }

    @Override
    public LobbyMode getLobbyMode() {
        return lobbyMode;
    }

    @Override
    public void setLobbyMode(LobbyMode lobbyMode) {
        this.lobbyMode = lobbyMode;
    }

    @Override
    public void addPlayer(Player player) {
        playerList.add(player);
    }

    @Override
    public Player removePlayer(int index) {
        return playerList.remove(index);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.game.setGameMode(gameMode);
    }

    @Override
    public GameMode getGameMode(){
        return this.game.getGameMode();
    }

    @Override
    public void setGameType(GameType gameType) {
        this.game.setGameType(gameType);
    }

    @Override
    public GameType getGameType(){return this.game.getGameType();}

    @Override
    public String getInvitationCode() {
        return this.invitationCode;
    }

    @Override
    public void startGame() {
        //this.game.start();
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
    public Player getHost() {
        return host;
    }

    @Override
    public Iterator<Player> iterator() {
        return playerList.iterator();
    }

    private Player generatePlayer(){

        long id = 0L;
        // Get all ids currently in use
        List<Long> idList = new LinkedList<>();
        for(Player player : playerList){
            idList.add(player.getPlayerId());
        }
        // if id already in use increase by 1
        while(idList.contains(id)){++id;}

        String username = "Player-"+id;

        String token = UUID.randomUUID().toString();

        return new Player(id, username, token);
    }
}
