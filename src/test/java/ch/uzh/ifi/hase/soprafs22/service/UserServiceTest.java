package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
    }

    private RegisteredUser testUser1;
    private RegisteredUser testUser2;
    private final long idNotInRepository = 999;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        /*
        Example mocks
        Mockito.when(userRepository.save(testUser1)).thenReturn(testUser1);
        Mockito.when(userRepository.findByUsername("testUsername1")).thenReturn(testUser1);
         */

        // given user1 in db
        testUser1 = new RegisteredUser();
        testUser1.setId(1L);
        testUser1.setToken("token1");
        testUser1.setPassword("password1");
        testUser1.setUsername("testUsername1");
        testUser1.setRankedScore(2000);
        testUser1.setWins(100);
        testUser1.setLosses(50);

        // given user2 in db
        testUser2 = new RegisteredUser();
        testUser2.setId(2L);
        testUser2.setToken("token2");
        testUser2.setPassword("password2");
        testUser2.setUsername("testUsername2");
        testUser2.setRankedScore(784932);
        testUser2.setWins(443);
        testUser2.setLosses(92);

        Mockito.when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        Mockito.when(userRepository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));

        Mockito.when(userRepository.findById(and(not(eq(testUser1.getId())), not(eq(testUser2.getId()))))).thenReturn(Optional.empty());

        Mockito.when(userRepository.findRegisteredUserByUsername(testUser1.getUsername())).thenReturn(testUser1);
        Mockito.when(userRepository.findRegisteredUserByUsername(testUser2.getUsername())).thenReturn(testUser2);

        Mockito.when(userRepository.findRegisteredUserByUsername(and(not(eq(testUser1.getUsername())), not(eq(testUser2.getUsername()))))).thenReturn(null);


    }

    @Test
    public void getRegisteredUser_withId_success() {

        // when
        RegisteredUser registeredUser = userService.getRegisteredUserWithId(testUser1.getId());

        // then
        assertEquals(testUser1.getId(), registeredUser.getId());
        assertEquals(testUser1.getToken(), registeredUser.getToken());
        assertEquals(testUser1.getPassword(), registeredUser.getPassword());
        assertEquals(testUser1.getUsername(), registeredUser.getUsername());
        assertEquals(testUser1.getRankedScore(), registeredUser.getRankedScore());
        assertEquals(testUser1.getWins(), registeredUser.getWins());
        assertEquals(testUser1.getLosses(), registeredUser.getLosses());
    }

    @Test
    public void getRegisteredUser_withIdNotInRepository_throwsException() {

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.getRegisteredUserWithId(idNotInRepository));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }

    @Test
    public void updateRegisteredUser_withId_success() {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = "newUsername";
        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(newPassword);

        // when
        userService.updateRegisteredUser(testUser1.getId(), testUser1.getToken(), newData);

        // then
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), newPassword);
        assertEquals(testUser1.getUsername(), newUserName);
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void updateRegisteredUser_withNoPasswordSet_success(String password) {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = "newUsername";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(password);

        // when
        userService.updateRegisteredUser(testUser1.getId(), testUser1.getToken(), newData);

        // then
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), newUserName);
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @Test
    public void updateRegisteredUser_withIdNotInRepository_throwsException() {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = "newUsername";
        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(newPassword);

        // when
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.updateRegisteredUser(idNotInRepository, testUser1.getToken(), newData));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        // Assert no change
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), oldData.getUsername());
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void updateRegisteredUser_noToken_throwsException(String token) {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = "newUsername";
        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(newPassword);

        // when
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.updateRegisteredUser(testUser1.getId(), token, newData));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());

        // Assert no change
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), oldData.getUsername());
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void updateRegisteredUser_nullOrEmptyNewUsername_throwsException(String name) {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(name);
        newData.setPassword(newPassword);

        // when
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.updateRegisteredUser(testUser1.getId(), testUser1.getToken(), newData));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        // Assert no change
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), oldData.getUsername());
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @Test
    public void updateRegisteredUser_TokenMissMatch_throwsException() {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = "newUsername";
        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(newPassword);

        // when
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.updateRegisteredUser(testUser1.getId(), "wrong_token", newData));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

        // Assert no change
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), oldData.getUsername());
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

    @Test
    public void updateRegisteredUser_NameConflict_throwsException() {

        RegisteredUser oldData = new RegisteredUser();
        oldData.setId(testUser1.getId());
        oldData.setToken(testUser1.getToken());
        oldData.setUsername(testUser1.getUsername());
        oldData.setPassword(testUser1.getPassword());
        oldData.setRankedScore(testUser1.getRankedScore());
        oldData.setWins(testUser1.getWins());
        oldData.setLosses(testUser1.getLosses());

        String newUserName = testUser2.getUsername();
        String newPassword = "newPassword";

        RegisteredUser newData = new RegisteredUser();
        newData.setUsername(newUserName);
        newData.setPassword(newPassword);

        // when
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> userService.updateRegisteredUser(testUser1.getId(), testUser1.getToken(), newData));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        // Assert no change
        assertEquals(testUser1.getId(), oldData.getId());
        assertEquals(testUser1.getToken(), oldData.getToken());
        assertEquals(testUser1.getPassword(), oldData.getPassword());
        assertEquals(testUser1.getUsername(), oldData.getUsername());
        assertEquals(testUser1.getRankedScore(), oldData.getRankedScore());
        assertEquals(testUser1.getWins(), oldData.getWins());
        assertEquals(testUser1.getLosses(), oldData.getLosses());
    }

}
