package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        /*
        Example mocks
        Mockito.when(userRepository.save(testUser1)).thenReturn(testUser1);
        Mockito.when(userRepository.findByUsername("testUsername1")).thenReturn(testUser1);
         */

    }

}
