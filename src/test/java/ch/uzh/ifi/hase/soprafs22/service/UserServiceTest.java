package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        testUser1.setToken("token");
        testUser1.setPassword("password");
        testUser1.setUsername("testUsername1");
        testUser1.setRankedScore(2000);
        testUser1.setWins(100);
        testUser1.setLosses(50);

        Mockito.when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        Mockito.when(userRepository.findById(not(eq(testUser1.getId())))).thenReturn(Optional.empty());

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

}
