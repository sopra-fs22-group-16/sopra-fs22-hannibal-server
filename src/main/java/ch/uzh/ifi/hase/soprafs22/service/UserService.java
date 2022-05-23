package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    private final UserRepository userRepository;

    private static final String TOKEN = "token";
    private static final String UPDATED = "updated";
    private static final String ACCESSED = "accessed";
    private static final String CREATED = "created";

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<RegisteredUser> getRegisteredUsers(String orderBy, boolean ascending, int pageNumber, int perPage) {

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

    public void updateRegisteredUser(Long id, String token, RegisteredUser userInput) {

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
            userToUpdate.setPassword(userInput.getPassword());
        }
    }

    private RegisteredUser getRegisteredUserByIdElseThrowNotFoundException(long id, String errorMessageEnding) {
        Optional<RegisteredUser> registeredUser = userRepository.findById(id);
        if (registeredUser.isEmpty()) {
            String errorMessageBeginning = String.format("The user with userId %d was not found.", id);
            throwResponseStatusException(HttpStatus.NOT_FOUND, errorMessageBeginning, errorMessageEnding);
        }
        return registeredUser.get();
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

    private void checkTokenMatches(RegisteredUser registeredUser, String token, String errorMessageEnding) {
        if (!registeredUser.getToken().equals(token)) {
            String errorMessageBeginning = "The provided authentication was incorrect.";
            throwResponseStatusException(HttpStatus.FORBIDDEN, errorMessageBeginning, errorMessageEnding);
        }
    }

    private void throwResponseStatusException(HttpStatus errorStatus, String errorMessageBeginning, String errorMessageEnding) {
        String errorMessage = errorMessageBeginning + " Therefore, the user(s) could not be " + errorMessageEnding + " !";
        throw new ResponseStatusException(errorStatus, errorMessage);
    }

    public RegisteredUser registerUser(RegisteredUser userInput) {
        checkStringNotNullOrEmpty(userInput.getUsername(), "username", CREATED);
        checkStringNotNullOrEmpty(userInput.getPassword(), "password", CREATED);
        checkDuplicateUsername(userInput.getUsername());
        userInput.setToken(UUID.randomUUID().toString());
        userInput.setLoggedIn(true);
        RegisteredUser registeredUser = userRepository.save(userInput);
        userRepository.flush();
        return registeredUser;
    }

    public RegisteredUser loginUser(RegisteredUser userInput) {
        checkStringNotNullOrEmpty(userInput.getUsername(), "username", ACCESSED);
        checkStringNotNullOrEmpty(userInput.getPassword(), "password", ACCESSED);
        RegisteredUser registeredUser = userRepository.findRegisteredUserByUsername(userInput.getUsername());
        if (registeredUser == null) {
            throwResponseStatusException(HttpStatus.NOT_FOUND, "The username provided does not exist.", ACCESSED);
        }
        if (!registeredUser.getPassword().equals(userInput.getPassword())) {
            throwResponseStatusException(HttpStatus.UNAUTHORIZED, "The password provided does not match.", ACCESSED);
        }
        registeredUser.setLoggedIn(true);
        registeredUser = userRepository.save(registeredUser);
        userRepository.flush();
        return registeredUser;
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
