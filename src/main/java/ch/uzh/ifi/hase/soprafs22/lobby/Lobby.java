package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.Game;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Lobby implements ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby {
    private long id;
    private String name;
    private LobbyMode lobbyMode;
    private Game game;
    private IUser host;
    private List<IUser> userList;
    private Map<Integer, Boolean> readyMap;
    private final String HANNIBAL_URL = "https://sopra-fs22-group-16-client.herokuapp.com?=";
    private final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code";

    public Lobby(String name, LobbyMode lobbyMode, IUser host) {
        this.name = name;
        this.lobbyMode = lobbyMode;
        this.host = host;
    }

    @Override
    public byte[] generateQrCode(String code) {
        String data = URLEncoder.encode(HANNIBAL_URL + code, StandardCharsets.UTF_8);
        RestTemplate restTemplate = new RestTemplate();
        String url = QR_API_URL + "/?data=" + data + "&size=100x100";
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
        return imageBytes;
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
    }

    @Override
    public IUser removeUser(int index) {
        return userList.remove(index);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        //this.game.setGameMode(gameMode);
    }

    @Override
    public void setGameType(GameType gameType) {
        //this.game.setGameType(gameType);
    }

    @Override
    public void startGame() {
        //this.game.start();
    }
}
