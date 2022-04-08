package ch.uzh.ifi.hase.soprafs22.repository;


import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByToken_success() {
        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setToken("token");
        registeredUser.setUsername("username");
        registeredUser.setPassword("password");
        registeredUser.setWins(10);
        registeredUser.setLosses(12);
        registeredUser.setRankedScore(120);

        entityManager.persist(registeredUser);
        entityManager.flush();

        // when
        RegisteredUser found = userRepository.findRegisteredUserByToken(registeredUser.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(registeredUser.getUsername(), found.getUsername());
        assertEquals(registeredUser.getToken(), found.getToken());
        assertEquals(registeredUser.getPassword(), found.getPassword());
        assertEquals(registeredUser.getWins(), found.getWins());
        assertEquals(registeredUser.getLosses(), found.getLosses());
        assertEquals(registeredUser.getRankedScore(), found.getRankedScore());
    }
}

