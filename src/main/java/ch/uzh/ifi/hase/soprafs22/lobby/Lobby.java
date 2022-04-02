package ch.uzh.ifi.hase.soprafs22.lobby;

import ch.uzh.ifi.hase.soprafs22.game.enums.GameMode;
import ch.uzh.ifi.hase.soprafs22.game.enums.GameType;
import ch.uzh.ifi.hase.soprafs22.lobby.enums.LobbyMode;
import ch.uzh.ifi.hase.soprafs22.lobby.interfaces.ILobby;
import ch.uzh.ifi.hase.soprafs22.user.IUser;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ch.uzh.ifi.hase.soprafs22.game.enums.GameMode.TWO_VS_TWO;
import static ch.uzh.ifi.hase.soprafs22.game.enums.GameType.UNRANKED;

public class Lobby implements ILobby {
    private GameType ranked;
    private GameMode mode;
    private long id;
    private String name;
    private LobbyMode lobbyMode;
    private IUser host;
    private final Map<Long, IUser> users = new HashMap<>();
    private final Map<Integer, Boolean> readyMap = new HashMap<>();
    private final String HANNIBAL_URL = "https://sopra-fs22-group-16-client.herokuapp.com?=";
    private final String QR_API_URL = "https://api.qrserver.com/v1/create-qr-code";

    public Lobby(String name, LobbyMode lobbyMode, IUser host) {
        this.name = name;
        this.lobbyMode = lobbyMode; //visibility
        this.host = host;
        this.mode =TWO_VS_TWO ;
        this.ranked = UNRANKED;
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
        users.put(user.getId(), user);
    }

    @Override
    public IUser removeUser(long userId) {
        return users.remove(userId);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.mode = gameMode;
    }

    @Override
    public void setGameType(GameType gameType) {
        this.ranked = gameType;
    }

    @Override
    public void startGame() {
        // How about creating a game with the stored parameters and starting it? It seems easier than the game dealing
        // with updates whenever the lobby changes.
        // this.game.start();
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
    public GameType getRanked() {
        return ranked;
    }

    @Override
    public GameMode getMode() {
        return mode;
    }

    @Override
    public IUser getHost() {
        return host;
    }

    @Override
    public long getId() {
        return id;
    }
}
