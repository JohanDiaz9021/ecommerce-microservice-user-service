package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.repository.CartRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_ReturnsListOfCartDto() {
        // Arrange
        Cart cartEntity = new Cart();
        cartEntity.setCartId(1);
        cartEntity.setUserId(100);
        when(cartRepository.findAll()).thenReturn(List.of(cartEntity));

        UserDto userDto = UserDto.builder()
                .userId(100)
                .firstName("Juan")
                .build();

        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/100", UserDto.class))
            .thenReturn(userDto);

        // Act
        List<CartDto> carts = cartService.findAll();

        // Assert
        assertNotNull(carts);
        assertEquals(1, carts.size());
        assertEquals(100, carts.get(0).getUserDto().getUserId());
        verify(cartRepository, times(1)).findAll();
        verify(restTemplate, times(1)).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void testFindById_Found_ReturnsCartDto() {
        // Arrange
        Cart cartEntity = new Cart();
        cartEntity.setCartId(2);
        cartEntity.setUserId(200);
        when(cartRepository.findById(2)).thenReturn(Optional.of(cartEntity));

        UserDto userDto = UserDto.builder()
                .userId(200)
                .firstName("Maria")
                .build();

        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/200", UserDto.class))
            .thenReturn(userDto);

        // Act
        CartDto cartDto = cartService.findById(2);

        // Assert
        assertNotNull(cartDto);
        assertEquals(2, cartDto.getCartId());
        assertEquals(200, cartDto.getUserDto().getUserId());
        verify(cartRepository, times(1)).findById(2);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> cartService.findById(99));
        verify(cartRepository, times(1)).findById(99);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testSave_CartDto_Success() {
        // Arrange
        Cart cartEntity = new Cart();
        cartEntity.setCartId(3);
        cartEntity.setUserId(300);

        CartDto cartDto = CartDto.builder()
                .cartId(3)
                .userId(300)
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cartEntity);

        // Act
        CartDto saved = cartService.save(cartDto);

        // Assert
        assertNotNull(saved);
        assertEquals(3, saved.getCartId());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testDeleteById_InvokesRepositoryDelete() {
        // Arrange
        doNothing().when(cartRepository).deleteById(4);

        // Act
        cartService.deleteById(4);

        // Assert
        verify(cartRepository, times(1)).deleteById(4);
        verifyNoInteractions(restTemplate);
    }
}
