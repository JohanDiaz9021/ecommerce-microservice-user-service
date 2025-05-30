package com.selimhorri.app.config.filter;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.selimhorri.app.config.filter.JwtRequestFilter;
import com.selimhorri.app.jwt.service.JwtService;

class JwtRequestFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtRequestFilter = new JwtRequestFilter(userDetailsService, jwtService);
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.extractUsername("valid-token")).thenReturn("user");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtService.validateToken("valid-token", userDetails)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
