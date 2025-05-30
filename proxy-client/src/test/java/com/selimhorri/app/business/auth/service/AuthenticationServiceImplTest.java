package com.selimhorri.app.business.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;

import com.selimhorri.app.business.auth.model.request.AuthenticationRequest;
import com.selimhorri.app.business.auth.service.impl.AuthenticationServiceImpl;
import com.selimhorri.app.exception.wrapper.IllegalAuthenticationCredentialsException;
import com.selimhorri.app.jwt.service.JwtService;

class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_shouldReturnJwt_whenCredentialsAreValid() {
        AuthenticationRequest request = new AuthenticationRequest("user", "pass");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("fake-jwt-token");

        assertEquals("fake-jwt-token", authenticationService.authenticate(request).getJwtToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticate_shouldThrowException_whenBadCredentials() {
        AuthenticationRequest request = new AuthenticationRequest("user", "wrongpass");
        doThrow(new BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any());

        assertThrows(IllegalAuthenticationCredentialsException.class,
            () -> authenticationService.authenticate(request));
    }
}
