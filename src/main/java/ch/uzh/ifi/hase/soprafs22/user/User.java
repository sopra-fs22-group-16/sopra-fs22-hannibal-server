package ch.uzh.ifi.hase.soprafs22.user;

import javax.persistence.*;

@Embeddable
public class User implements IUser {

    @Id
    @GeneratedValue
    private long id;

    private String token;

    @Column(nullable = false, unique = true)
    private String username;

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
