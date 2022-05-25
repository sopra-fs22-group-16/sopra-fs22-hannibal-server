package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.RegisteredUserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.RegisteredUserPageGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.UserLoginGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.get_dto.UserRegistrationGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.put_dto.RegisteredUserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.LinkedList;
import java.util.List;

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

    @PostMapping("/{apiVersion}/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserRegistrationGetDTO registerUser(@RequestBody RegisteredUserPutDTO unregisteredUserPutDTO){
        RegisteredUser userInput = DTOMapper.INSTANCE.convertRegisteredUserPutDTOToRegisteredUser(unregisteredUserPutDTO);

        RegisteredUser registeredUser = userService.registerUser(userInput);

        return DTOMapper.INSTANCE.convertRegisteredUserToUserRegistrationGetDTO(registeredUser);
    }

    @PostMapping("/{apiVersion}/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginGetDTO loginUser(@RequestBody RegisteredUserPutDTO registeredUserPutDTO){
        RegisteredUser userInput = DTOMapper.INSTANCE.convertRegisteredUserPutDTOToRegisteredUser(registeredUserPutDTO);

        RegisteredUser registeredUser = userService.loginUser(userInput);

        return DTOMapper.INSTANCE.convertRegisteredUserToUserLoginGetDTO(registeredUser);
    }

    @GetMapping("/{apiVersion}/users")
    @ResponseStatus(HttpStatus.OK)
    @Validated
    @ResponseBody
    public RegisteredUserPageGetDTO getUsers(@RequestParam(name = "sortBy", defaultValue = "RANKED_SCORE") String sortBy,
                                             @RequestParam(name = "ascending", defaultValue = "true") boolean ascending,
                                             @RequestParam(name = "pageNumber", defaultValue = "0") @Min(0) int pageNumber,
                                             @RequestParam(name = "perPage", defaultValue = "10") @Min(1) @Max(50) int perPage) {

        List<RegisteredUser> registeredUsers = userService.getRegisteredUsers(sortBy, ascending, pageNumber, perPage);
        long totalRegisteredUsers = userService.getTotalRegisteredUsers();

        return DTOMapper.INSTANCE.convertToRegisteredUserPageGetDTO(perPage, totalRegisteredUsers, registeredUsers);
    }

    @GetMapping("/{apiVersion}/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RegisteredUserGetDTO getUserById(@PathVariable Long id) {

        RegisteredUser registeredUser = userService.getRegisteredUserWithId(id);

        return DTOMapper.INSTANCE.convertRegisteredUserToRegisteredUserGetDTO(registeredUser);
    }

    @PutMapping("/{apiVersion}/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUserById(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody RegisteredUserPutDTO registeredUserPutDTO) {
        // convert API user to internal representation
        RegisteredUser userInput = DTOMapper.INSTANCE.convertRegisteredUserPutDTOToRegisteredUser(registeredUserPutDTO);

        // update user
        userService.updateRegisteredUser(id, token, userInput);
    }

}
