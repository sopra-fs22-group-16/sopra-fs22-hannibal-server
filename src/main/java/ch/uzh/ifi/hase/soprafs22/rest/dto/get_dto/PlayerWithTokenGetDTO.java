package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

public class PlayerWithTokenGetDTO {

    private Long id;
    private String name;
    private boolean ready;
    private int team;
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}
}
