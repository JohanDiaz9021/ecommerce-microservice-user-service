package com.selimhorri.app.service;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testSaveUser() {
        UserDto inputDto = new UserDto();
        User domainUser = new User();
        UserDto expectedDto = new UserDto();

        when(userRepository.save(any(User.class))).thenReturn(domainUser);

        try (MockedStatic<UserMappingHelper> mockedStatic = mockStatic(UserMappingHelper.class)) {
            mockedStatic.when(() -> UserMappingHelper.map(inputDto)).thenReturn(domainUser);
            mockedStatic.when(() -> UserMappingHelper.map(domainUser)).thenReturn(expectedDto);

            UserDto result = userService.save(inputDto);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void testFindUserByIdSuccess() {
        Integer id = 1;
        User domainUser = new User();
        UserDto expectedDto = new UserDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(domainUser));

        try (MockedStatic<UserMappingHelper> mockedStatic = mockStatic(UserMappingHelper.class)) {
            mockedStatic.when(() -> UserMappingHelper.map(domainUser)).thenReturn(expectedDto);

            UserDto result = userService.findById(id);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void testDeleteById() {
        Integer id = 1;
        doNothing().when(userRepository).deleteById(id);
        userService.deleteById(id);
        verify(userRepository, times(1)).deleteById(id);
    }
}
