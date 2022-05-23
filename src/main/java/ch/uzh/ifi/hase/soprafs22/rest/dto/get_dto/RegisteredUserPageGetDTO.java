package ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto;

import java.util.LinkedList;

public class RegisteredUserPageGetDTO {

    private int start;
    private int limit;
    private int length;
    private int total;
    private LinkedList<RegisteredUserGetDTO> users;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public LinkedList<RegisteredUserGetDTO> getUsers() {
        return users;
    }

    public void setUsers(LinkedList<RegisteredUserGetDTO> users) {
        this.users = users;
    }
}
