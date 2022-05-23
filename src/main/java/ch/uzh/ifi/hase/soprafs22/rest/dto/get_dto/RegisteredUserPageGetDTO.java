package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

import java.util.List;

public class RegisteredUserPageGetDTO {

    private int limit;
    private int length;
    private long total;
    private List<RegisteredUserGetDTO> users;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<RegisteredUserGetDTO> getUsers() {
        return users;
    }

    public void setUsers(List<RegisteredUserGetDTO> users) {
        this.users = users;
    }
}
