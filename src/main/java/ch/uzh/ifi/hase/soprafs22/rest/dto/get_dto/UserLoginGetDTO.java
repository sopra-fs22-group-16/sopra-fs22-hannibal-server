package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

public class UserLoginGetDTO {

    private long id;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
