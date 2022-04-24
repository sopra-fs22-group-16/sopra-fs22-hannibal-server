package ch.uzh.ifi.hase.soprafs22.service.Integration;

import ch.uzh.ifi.hase.soprafs22.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {
    @Autowired
    GameService gameService;

    @BeforeEach
    public void setup() {

    }

    @Test
    
}
