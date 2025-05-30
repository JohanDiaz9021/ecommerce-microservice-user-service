package com.selimhorri.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.repository.PaymentRepository;

@SpringBootTest
@ActiveProfiles("test") 
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll(); // limpia BD antes de cada prueba
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }


    @Test
    void testUpdatePayment() {
        Payment p = Payment.builder()
            .orderId(10)
            .isPayed(false)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.NOT_STARTED)
            .build();

        Payment saved = paymentRepository.save(p);

        PaymentDto updateDto = PaymentDto.builder()
            .paymentId(saved.getPaymentId())
            .isPayed(true)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.COMPLETED)
            .orderDto(OrderDto.builder().orderId(10).build())
            .build();

        PaymentDto updated = paymentService.update(updateDto);

        assertEquals(saved.getPaymentId(), updated.getPaymentId());
        assertTrue(updated.getIsPayed());
        assertEquals(com.selimhorri.app.domain.PaymentStatus.COMPLETED, updated.getPaymentStatus());
    }

    @Test
    void testDeleteById() {
        Payment p = Payment.builder()
            .orderId(20)
            .isPayed(false)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.NOT_STARTED)
            .build();

        Payment saved = paymentRepository.save(p);
        Integer id = saved.getPaymentId();

        paymentService.deleteById(id);

        assertFalse(paymentRepository.findById(id).isPresent());
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(com.selimhorri.app.exception.wrapper.PaymentNotFoundException.class,
                () -> paymentService.findById(9999));
    }
}
