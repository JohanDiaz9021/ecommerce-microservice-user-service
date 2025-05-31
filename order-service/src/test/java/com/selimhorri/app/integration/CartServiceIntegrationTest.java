/*package com.selimhorri.app.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.repository.CartRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CartServiceIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        cartRepository.deleteAll(); // Limpiar BD antes de cada test
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenFindAllCalled_thenUserServiceIsCalledAndCartDtosReturned() throws Exception {
        // Arrange: crea carrito en BD
        var cart = new com.selimhorri.app.domain.Cart();
        cart.setUserId(1);
        cartRepository.save(cart);

        // Simula respuesta JSON del servicio usuario
        UserDto userDto = UserDto.builder()
            .userId(1)
            .firstName("IntegrationUser")
            .build();

        String userJson = objectMapper.writeValueAsString(userDto);

        mockServer.expect(requestTo(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/1"))
            .andExpect(method(org.springframework.http.HttpMethod.GET))
            .andRespond(withSuccess(userJson, MediaType.APPLICATION_JSON));

        // Act: llama al endpoint REST real /api/carts
        mockMvc.perform(get("/api/carts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
            .andExpect(jsonPath("$[0].userDto.userId").value(1))

        // Verifica que el mock se haya usado
        mockServer.verify();
    }

    @Test
    void whenFindByIdCalled_thenUserServiceCalledAndCartDtoReturned() throws Exception {
        // Arrange
        var cart = new com.selimhorri.app.domain.Cart();
        cart.setUserId(2);
        var saved = cartRepository.save(cart);

        UserDto userDto = UserDto.builder()
            .userId(2)
            .firstName("IntegrationUser2")
            .build();

        String userJson = objectMapper.writeValueAsString(userDto);

        mockServer.expect(requestTo(AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/2"))
            .andExpect(method(org.springframework.http.HttpMethod.GET))
            .andRespond(withSuccess(userJson, MediaType.APPLICATION_JSON));

        // Act & Assert
        mockMvc.perform(get("/api/carts/" + saved.getCartId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userDto.userId").value(2))
            .andExpect(jsonPath("$.userDto.firstName").value("IntegrationUser2"));

        mockServer.verify();
    }

    @Test
    void whenFindByIdNotFound_thenReturn404() throws Exception {
        mockMvc.perform(get("/api/carts/9999"))
            .andExpect(status().isBadRequest());
    }

}*/
