package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    private final UserRepository userRepository;

    private static final String TOKEN = "token";

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



}
