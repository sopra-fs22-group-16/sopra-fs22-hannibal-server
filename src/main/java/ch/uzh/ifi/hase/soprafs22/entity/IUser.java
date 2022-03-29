package ch.uzh.ifi.hase.soprafs22.entity;

public interface IUser {

    void setId(Long id);
    Long getId();

    void setToken(String token);
    String getToken();

    void setUsername(String username);
    String getUsername();
}
