package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Lobby implements ILobby {

    private final long id;
    private String name;
    private LobbyMode lobbyMode;
    private Game game;
    private IUser host;
    private final List<IUser> userList;
    private final Map<IUser, Boolean> readyMap;
    private final String HANNIBAL_URL = "https://sopra-fs22-group-16-client.herokuapp.com?=";
    private final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code";

    public Lobby(Long id, String name, LobbyMode lobbyMode, IUser host) {
        this.id = id;
        this.name = name;
        this.lobbyMode = lobbyMode;
        this.game = new Game(GameMode.ONE_VS_ONE, GameType.UNRANKED);
        this.host = host;
        this.userList = new LinkedList<>();
        userList.add(host);
        this.readyMap = new HashMap<>();
        readyMap.put(host, false);
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
    public void addUser(IUser user) {
        userList.add(user);
        readyMap.put(user, false);
    }

    @Override
    public IUser removeUser(int index) {
        IUser user = userList.remove(index);
        readyMap.remove(user);
        return user;
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
    public IUser getHost() {
        return host;
    }

    @Override
    public boolean isUserReady(IUser user){
        return readyMap.containsKey(user) && readyMap.get(user);
    }

    @Override
    public Iterator<IUser> iterator() {
        return userList.iterator();
    }

}
