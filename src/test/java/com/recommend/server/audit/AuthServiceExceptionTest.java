package com.recommend.server.audit;

import com.recommend.server.exception.BadCredentials;
import com.recommend.server.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthServiceExceptionTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("FIXED CRIT-4: getUserDTO without auth throws BadCredentials")
    void getUserDTOWithoutAuthShouldThrowBadCredentials() {
        assertThrows(BadCredentials.class, () -> authService.getUserDTO());
    }

    @Test
    @DisplayName("FIXED CRIT-4: getUserEmail handles non-String principal (e.g. @WithMockUser)")
    @WithMockUser(username = "nonexistent@test.com")
    void getUserDTOWithMockUserNowReturnsBadCredentials() {
        assertThrows(BadCredentials.class, () -> authService.getUserDTO());
    }

    @Test
    @DisplayName("FIXED CRIT-4: getUserDTO with non-existent user now throws BadCredentials instead of NoSuchElementException")
    void getUserDTOWithNonExistentUserThrowsBadCredentials() {
        var auth = new UsernamePasswordAuthenticationToken(
                "nonexistent@test.com", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(BadCredentials.class, () -> authService.getUserDTO());

        SecurityContextHolder.clearContext();
    }
}
