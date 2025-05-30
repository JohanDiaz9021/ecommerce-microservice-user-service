package com.selimhorri.app.business.favourite.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FavouriteDtoTest {

    @Test
    void testFavouriteDtoCreation() {
        FavouriteDto favourite = FavouriteDto.builder()
            .userId(1)
            .productId(2)
            .likeDate(LocalDateTime.now())
            .build();

        assertEquals(1, favourite.getUserId());
        assertEquals(2, favourite.getProductId());
        assertNotNull(favourite.getLikeDate());
    }
}
