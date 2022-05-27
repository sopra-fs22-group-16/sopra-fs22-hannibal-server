package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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

    private final PasswordEncoder passwordEncoder;

    private final DaoAuthenticationProvider authProvider;

    private final UserRepository userRepository;

    private static final String UPDATED = "updated";
    private static final String ACCESSED = "accessed";
    private static final String CREATED = "created";

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, DaoAuthenticationProvider authProvider, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authProvider = authProvider;
        this.userRepository = userRepository;
    }

    public List<RegisteredUser> getRegisteredUsers(@NotNull String orderBy, boolean ascending, int pageNumber, int perPage) {

        Pageable page = PageRequest.of(pageNumber, perPage);

        switch (orderBy) {
            case "RANKED_SCORE" -> {
                if (ascending) {
                    return userRepository.findAllByOrderByRankedScoreAsc(page);
                }
                else {
                    return userRepository.findAllByOrderByRankedScoreDesc(page);
                }
            }
            case "WINS" -> {
                if (ascending) {
                    return userRepository.findAllByOrderByWinsAsc(page);
                }
                else {
                    return userRepository.findAllByOrderByWinsDesc(page);
                }
            }
            case "LOSSES" -> {
                if (ascending) {
                    return userRepository.findAllByOrderByLossesAsc(page);
                }
                else {
                    return userRepository.findAllByOrderByLossesDesc(page);
                }
            }
            default -> throwResponseStatusException(HttpStatus.BAD_REQUEST, "The data can not be sorted by the field" + orderBy, ACCESSED);
        }
        return new LinkedList<>();
    }

    public long getTotalRegisteredUsers() {
        return userRepository.count();
    }

    public RegisteredUser getRegisteredUserWithId(long id) {
        return getRegisteredUserByIdElseThrowNotFoundException(id, ACCESSED);
    }

    public void updateRegisteredUser(Long id, String token, @NotNull RegisteredUser userInput) {

        checkTokenNullOrEmpty(token, UPDATED);

        RegisteredUser userToUpdate = getRegisteredUserByIdElseThrowNotFoundException(id, UPDATED);

        checkTokenMatches(userToUpdate, token, UPDATED);

        checkStringNotNullOrEmpty(userInput.getUsername(), "username", UPDATED);

        if (!userToUpdate.getUsername().equals(userInput.getUsername())) {
            RegisteredUser potentialRegisteredUserWithSameNewName = userRepository.findRegisteredUserByUsername(userInput.getUsername());
            if (potentialRegisteredUserWithSameNewName != null && !potentialRegisteredUserWithSameNewName.equals(userToUpdate)) {
                String errorMessageBeginning = String.format("The username %s is not unique.", userInput.getUsername());
                throwResponseStatusException(HttpStatus.CONFLICT, errorMessageBeginning, UPDATED);
            }
        }

        userToUpdate.setUsername(userInput.getUsername());
        // Update password only if it is set
        if (userInput.getPassword() != null && !userInput.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(userInput.getPassword()));
        }

        userRepository.save(userToUpdate);
        userRepository.flush();
    }

    private @NotNull RegisteredUser getRegisteredUserByIdElseThrowNotFoundException(long id, String errorMessageEnding) {
        Optional<RegisteredUser> optionalRegisteredUser = userRepository.findById(id);
        if (optionalRegisteredUser.isEmpty()) {
            String errorMessageBeginning = String.format("The user with userId %d was not found.", id);
            throwResponseStatusException(HttpStatus.NOT_FOUND, errorMessageBeginning, errorMessageEnding);
        }
        return optionalRegisteredUser.get();
    }

    public RegisteredUser registerUser(@NotNull RegisteredUser userInput) {
        checkStringNotNullOrEmpty(userInput.getUsername(), "username", CREATED);
        checkStringNotNullOrEmpty(userInput.getPassword(), "password", CREATED);
        checkDuplicateUsername(userInput.getUsername());
        userInput.setPassword(passwordEncoder.encode(userInput.getPassword()));
        userInput.setToken(UUID.randomUUID().toString());
        userInput = userRepository.save(userInput);
        userRepository.flush();
        return userInput;
    }

    public RegisteredUser userLogin(@NotNull RegisteredUser loggedOutUser) {
        checkStringNotNullOrEmpty(loggedOutUser.getUsername(), "username", ACCESSED);
        checkStringNotNullOrEmpty(loggedOutUser.getPassword(), "password", ACCESSED);
        try {
            Authentication authentication = authProvider.authenticate(new UsernamePasswordAuthenticationToken(loggedOutUser, loggedOutUser.getPassword()));
            return (RegisteredUser) authentication.getPrincipal();
        }
        catch (BadCredentialsException e) {
            throwResponseStatusException(HttpStatus.NOT_FOUND, "The credentials provided are not correct.", ACCESSED);
        }
        return loggedOutUser;
    }

    public RegisteredUser userLogout(@NotNull RegisteredUser loggedInUser) {
        RegisteredUser registeredUser = userRepository.findRegisteredUserByUsername(loggedInUser.getUsername());
        Authentication auth = new UsernamePasswordAuthenticationToken(registeredUser, registeredUser.getPassword());
        auth.setAuthenticated(false);
        return (RegisteredUser) auth.getPrincipal();
    }

    private void checkTokenNullOrEmpty(String token, String errorMessageEnding) {
        if (token == null || token.isEmpty()) {
            String errorMessageBeginning = "The user needs to provide authentication to update user information.";
            throwResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessageBeginning, errorMessageEnding);
        }
    }

    private void checkStringNotNullOrEmpty(String s, String fieldName, String errorMessageEnding) {
        if (s == null || s.isEmpty()) {
            String errorMessageBeginning = "The field " + fieldName + " was null or empty.";
            throwResponseStatusException(HttpStatus.BAD_REQUEST, errorMessageBeginning, errorMessageEnding);
        }
    }

    private void checkTokenMatches(@NotNull RegisteredUser registeredUser, String token, String errorMessageEnding) {
        if (!registeredUser.getToken().equals(token)) {
            String errorMessageBeginning = "The provided authentication was incorrect.";
            throwResponseStatusException(HttpStatus.FORBIDDEN, errorMessageBeginning, errorMessageEnding);
        }
    }

    private void throwResponseStatusException(HttpStatus errorStatus, String errorMessageBeginning, String errorMessageEnding) {
        String errorMessage = errorMessageBeginning + " Therefore, the user(s) could not be " + errorMessageEnding + " !";
        throw new ResponseStatusException(errorStatus, errorMessage);
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name defined in the User
     * entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param desiredUserName The desired username of the new user
     * @throws org.springframework.web.server.ResponseStatusException The username is already taken.
     * @see RegisteredUser
     */
    private void checkDuplicateUsername(String desiredUserName) {
        RegisteredUser userByUsername = userRepository.findRegisteredUserByUsername(desiredUserName);

        if (userByUsername != null) {
            throwResponseStatusException(HttpStatus.CONFLICT, "The username provided is already taken.", "created");
        }
    }
}
