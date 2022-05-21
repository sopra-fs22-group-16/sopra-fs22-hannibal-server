package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.RegisteredUserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the registered users.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */

@RestController
public class UserController {

    @Value("${api.version}")
    private String apiVersion;

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{apiVersion}/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RegisteredUserGetDTO getUserById(@PathVariable Long id) {

        System.out.println("----------------------------");

        System.out.println(id);

        System.out.println(id == 1L);

        System.out.println("----------------------------");

        RegisteredUser registeredUser = userService.getRegisteredUserWithId(id);

        return DTOMapper.INSTANCE.convertRegisteredUserToRegisteredUserGetDTO(registeredUser);
    }

}
