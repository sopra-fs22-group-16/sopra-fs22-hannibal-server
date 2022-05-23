package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserRegistrationGetDTO {
    private long id;
    private String username;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date creationDate;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
