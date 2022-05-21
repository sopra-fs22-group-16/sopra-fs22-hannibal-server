package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.user.RegisteredUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getRegisteredUserWithId_validInput_thenReturnJsonArray() throws Exception {
        // given
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(1L);
        registeredUser.setToken("token");
        registeredUser.setUsername("username");
        registeredUser.setPassword("password");
        registeredUser.setRankedScore(1500);
        registeredUser.setWins(10);
        registeredUser.setLosses(20);

        // this mocks the UserService -> we define above what the userService should
        // return when getRegisteredUser() is called
        given(userService.getRegisteredUserWithId(1L)).willReturn(registeredUser);

        // when
        MockHttpServletRequestBuilder getRequest = get("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(registeredUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(registeredUser.getUsername())))
                .andExpect(jsonPath("$.rankedScore", is(registeredUser.getRankedScore())))
                .andExpect(jsonPath("$.wins", is(registeredUser.getWins())))
                .andExpect(jsonPath("$.losses", is(registeredUser.getLosses())));

    }

}
