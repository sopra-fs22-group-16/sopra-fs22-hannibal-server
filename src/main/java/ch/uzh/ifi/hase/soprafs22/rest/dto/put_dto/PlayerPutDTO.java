package ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto;

public class PlayerPutDTO {
    private String name;
    private Boolean ready; // null, true, false

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }
}
