package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.beans.factory.annotation.Value;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the registered users.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
public class UserController {

    @Value("${api.version}")
    private String apiVersion;

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

}
