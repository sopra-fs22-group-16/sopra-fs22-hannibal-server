package ch.uzh.ifi.hase.soprafs22.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
