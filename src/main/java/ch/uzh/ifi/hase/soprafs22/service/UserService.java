package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the users
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private static final String TOKEN = "token";

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



}
