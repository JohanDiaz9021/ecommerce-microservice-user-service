package com.selimhorri.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.web.client.ExpectedCount;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
public class FavouriteServiceIntegrationTest {

    @Autowired
    private FavouriteServiceImpl favouriteService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    private Favourite favouriteEntity;

    private FavouriteId favouriteId;

    @BeforeEach
    public void setup() {
        // Inicializa MockRestServiceServer para interceptar las llamadas RestTemplate
        mockServer = MockRestServiceServer.createServer(restTemplate);

        favouriteId = new FavouriteId(1, 10,
                LocalDateTime.parse("20-05-2025__14:30:00:000000",
                        java.time.format.DateTimeFormatter.ofPattern(AppConstant.LOCAL_DATE_TIME_FORMAT)));

        favouriteEntity = Favourite.builder()
                .userId(favouriteId.getUserId())
                .productId(favouriteId.getProductId())
                .likeDate(favouriteId.getLikeDate())
                .build();

        // Limpia DB para pruebas, si usas JPA repo, asegurate de hacerlo
        // Por ejemplo, puedes borrar todos favoritos previos si hay método disponible
    }

    @Test
    public void testFindAll_withMockedUserAndProductServices() throws Exception {
        // Primero guarda un favorito real en BD
        favouriteService.save(FavouriteDto.builder()
                .userId(favouriteEntity.getUserId())
                .productId(favouriteEntity.getProductId())
                .likeDate(favouriteEntity.getLikeDate())
                .build());

        // Mockea respuesta para USER_SERVICE
        String userJson = objectMapper.writeValueAsString(
                new com.selimhorri.app.dto.UserDto(1, "John", "Doe", null, "john@example.com", "123456", null));

        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(userJson, MediaType.APPLICATION_JSON));

        // Mockea respuesta para PRODUCT_SERVICE
        String productJson = objectMapper.writeValueAsString(
                new com.selimhorri.app.dto.ProductDto(10, "Product X", null, "SKU123", 99.99, 10, null));

        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(productJson, MediaType.APPLICATION_JSON));

        // Ejecuta servicio y valida la integración
        List<FavouriteDto> favourites = favouriteService.findAll();

        assertThat(favourites).isNotEmpty();
        FavouriteDto first = favourites.get(0);
        assertEquals(1, first.getUserId());
        assertEquals(10, first.getProductId());
        assertNotNull(first.getUserDto());
        assertEquals("John", first.getUserDto().getFirstName());
        assertNotNull(first.getProductDto());
        assertEquals("Product X", first.getProductDto().getProductTitle());

        mockServer.verify();
    }

    @Test
    public void testFindById_withMockedUserAndProductServices() throws Exception {
        // Guarda favorito
        favouriteService.save(FavouriteDto.builder()
                .userId(favouriteEntity.getUserId())
                .productId(favouriteEntity.getProductId())
                .likeDate(favouriteEntity.getLikeDate())
                .build());

        // Mock USER_SERVICE
        String userJson = objectMapper.writeValueAsString(
                new com.selimhorri.app.dto.UserDto(1, "Jane", "Smith", null, "jane@example.com", "987654", null));

        mockServer.expect(requestTo(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(userJson, MediaType.APPLICATION_JSON));

        // Mock PRODUCT_SERVICE
        String productJson = objectMapper.writeValueAsString(
                new com.selimhorri.app.dto.ProductDto(10, "Product Y", null, "SKU456", 55.55, 5, null));

        mockServer.expect(requestTo(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(productJson, MediaType.APPLICATION_JSON));

        // Test findById
        FavouriteDto dto = favouriteService.findById(favouriteId);

        assertNotNull(dto);
        assertEquals(1, dto.getUserId());
        assertEquals(10, dto.getProductId());
        assertEquals("Jane", dto.getUserDto().getFirstName());
        assertEquals("Product Y", dto.getProductDto().getProductTitle());

        mockServer.verify();
    }

    @Test
    public void testSaveAndDelete_integration() {
        FavouriteDto toSave = FavouriteDto.builder()
                .userId(99)
                .productId(88)
                .likeDate(LocalDateTime.now())
                .build();

        // Guarda favorito
        FavouriteDto saved = favouriteService.save(toSave);
        assertNotNull(saved);
        assertEquals(toSave.getUserId(), saved.getUserId());

        // Borra favorito
        assertDoesNotThrow(() -> favouriteService.deleteById(
                new FavouriteId(saved.getUserId(), saved.getProductId(), saved.getLikeDate())));

        // Confirmar que ya no existe
        assertThrows(RuntimeException.class, () -> favouriteService.findById(
                new FavouriteId(saved.getUserId(), saved.getProductId(), saved.getLikeDate())));
    }

    // Puedes agregar tests similares para update o buscar con findById no existente.

}
