package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.repository.FavouriteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

public class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private Favourite favouriteEntity;
    private FavouriteDto favouriteDto;
    private FavouriteId favouriteId;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        favouriteId = new FavouriteId(1, 2,
            LocalDateTime.parse("20-05-2025__14:30:00:000000", 
                java.time.format.DateTimeFormatter.ofPattern(AppConstant.LOCAL_DATE_TIME_FORMAT)));

        favouriteEntity = Favourite.builder()
                .userId(1)
                .productId(2)
                .likeDate(favouriteId.getLikeDate())
                .build();

        favouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(2)
                .likeDate(favouriteId.getLikeDate())
                .build();
    }

    @Test
    public void testFindAll_returnsList() {
        when(favouriteRepository.findAll()).thenReturn(List.of(favouriteEntity));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(
                UserDto.builder().userId(1).firstName("TestUser").build());
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(
                ProductDto.builder().productId(2).productTitle("TestProduct").build());

        List<FavouriteDto> result = favouriteService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        FavouriteDto dto = result.get(0);
        assertEquals(1, dto.getUserId());
        assertEquals(2, dto.getProductId());
        assertNotNull(dto.getUserDto());
        assertNotNull(dto.getProductDto());
        assertEquals("TestUser", dto.getUserDto().getFirstName());
        assertEquals("TestProduct", dto.getProductDto().getProductTitle());
    }

    @Test
    public void testFindById_found() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favouriteEntity));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(
                UserDto.builder().userId(1).firstName("TestUser").build());
        when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(
                ProductDto.builder().productId(2).productTitle("TestProduct").build());

        FavouriteDto result = favouriteService.findById(favouriteId);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(2, result.getProductId());
        assertNotNull(result.getUserDto());
        assertNotNull(result.getProductDto());
        assertEquals("TestUser", result.getUserDto().getFirstName());
    }

    @Test
    public void testFindById_notFound() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        FavouriteNotFoundException thrown = assertThrows(FavouriteNotFoundException.class,
                () -> favouriteService.findById(favouriteId));

        assertTrue(thrown.getMessage().contains("not found"));
    }

    @Test
    public void testSave_success() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favouriteEntity);

        FavouriteDto result = favouriteService.save(favouriteDto);

        assertNotNull(result);
        assertEquals(favouriteDto.getUserId(), result.getUserId());
        assertEquals(favouriteDto.getProductId(), result.getProductId());
    }

    @Test
    public void testDeleteById_success() {
        // No return, just verify that deleteById is called
        doNothing().when(favouriteRepository).deleteById(favouriteId);

        assertDoesNotThrow(() -> favouriteService.deleteById(favouriteId));

        verify(favouriteRepository, times(1)).deleteById(favouriteId);
    }
}
