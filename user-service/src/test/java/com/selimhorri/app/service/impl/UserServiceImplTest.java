package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userEntity = User.builder()
                .userId(1)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .build();
    }


    @Test
    public void testSave_nullInput_throwsException() {
        assertThrows(NullPointerException.class, () -> userService.save(null));
    }

    @Test
    public void testUpdate_nullInput_throwsException() {
        assertThrows(NullPointerException.class, () -> userService.update(null));
    }


    @Test
    public void testFindById_notFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserObjectNotFoundException.class, () -> userService.findById(1));
    }


    @Test
    public void testDeleteById_callsRepository() {
        doNothing().when(userRepository).deleteById(1);

        userService.deleteById(1);

        verify(userRepository, times(1)).deleteById(1);
    }



    @Test
    public void testFindByUsername_notFound() {
        when(userRepository.findByCredentialUsername("juan123")).thenReturn(Optional.empty());

        assertThrows(UserObjectNotFoundException.class, () -> userService.findByUsername("juan123"));
    }

}
