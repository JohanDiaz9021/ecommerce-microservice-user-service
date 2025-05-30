package com.selimhorri.app.business.favourite.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import com.selimhorri.app.business.favourite.controller.FavouriteController;
import com.selimhorri.app.business.favourite.model.FavouriteDto;
import com.selimhorri.app.business.favourite.model.response.FavouriteFavouriteServiceCollectionDtoResponse;
import com.selimhorri.app.business.favourite.service.FavouriteClientService;

class FavouriteControllerTest {

    @Mock
    private FavouriteClientService favouriteClientService;

    @InjectMocks
    private FavouriteController favouriteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnFavouritesCollection() {
        FavouriteFavouriteServiceCollectionDtoResponse responseDto = FavouriteFavouriteServiceCollectionDtoResponse.builder()
            .collection(java.util.Collections.emptyList())
            .build();
        when(favouriteClientService.findAll()).thenReturn(ResponseEntity.ok(responseDto));

        ResponseEntity<FavouriteFavouriteServiceCollectionDtoResponse> response = favouriteController.findAll();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void save_shouldReturnSavedFavourite() {
        FavouriteDto favourite = FavouriteDto.builder()
            .userId(1)
            .productId(2)
            .likeDate(LocalDateTime.now())
            .build();

        when(favouriteClientService.save(favourite)).thenReturn(ResponseEntity.ok(favourite));

        ResponseEntity<FavouriteDto> response = favouriteController.save(favourite);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(favourite, response.getBody());
    }
}
