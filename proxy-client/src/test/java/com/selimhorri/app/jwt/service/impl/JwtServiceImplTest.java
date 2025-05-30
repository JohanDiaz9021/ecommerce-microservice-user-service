package com.selimhorri.app.jwt.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;

import com.selimhorri.app.jwt.service.impl.JwtServiceImpl;
import com.selimhorri.app.jwt.util.JwtUtil;

class JwtServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void extractUsername_shouldReturnUsername() {
        when(jwtUtil.extractUsername("token")).thenReturn("user");
        assertEquals("user", jwtService.extractUsername("token"));
    }

}
