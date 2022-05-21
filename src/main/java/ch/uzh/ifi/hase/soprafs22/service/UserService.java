package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisteredUser getRegisteredUserWithId(long id) {
        return getRegisteredUserByIdElseThrowNotFoundException(id, ACCESSED);
    }

    public void updateRegisteredUser(Long id, String token, RegisteredUser userInput) {

        checkTokenNullOrEmpty(token, UPDATED);

        RegisteredUser userToUpdate = getRegisteredUserByIdElseThrowNotFoundException(id, UPDATED);

        checkTokenMatches(userToUpdate, token, UPDATED);

        checkStringNotNullOrEmpty(userInput.getUsername(), "username", UPDATED);

        if(!userToUpdate.getUsername().equals(userInput.getUsername())){
            RegisteredUser potentialRegisteredUserWithSameNewName = userRepository.findRegisteredUserByUsername(userInput.getUsername());
            if(potentialRegisteredUserWithSameNewName != null && !potentialRegisteredUserWithSameNewName.equals(userToUpdate)){
                String errorMessageBeginning = String.format("The username %s is not unique.", userInput.getUsername());
                throwResponseStatusException(HttpStatus.CONFLICT, errorMessageBeginning, UPDATED);
            }
        }

        userToUpdate.setUsername(userInput.getUsername());
        // Update password only if it is set
        if(userInput.getPassword() != null && !userInput.getPassword().isEmpty()){
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
        String errorMessage = errorMessageBeginning + " Therefore, the user could not be " + errorMessageEnding + " !";
        throw new ResponseStatusException(errorStatus, errorMessage);
    }
}
