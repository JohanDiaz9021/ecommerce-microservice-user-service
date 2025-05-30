package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment1;
    private Payment payment2;
    private PaymentDto paymentDto1;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        payment1 = Payment.builder()
            .paymentId(1)
            .orderId(100)
            .isPayed(true)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.COMPLETED)
            .build();

        payment2 = Payment.builder()
            .paymentId(2)
            .orderId(101)
            .isPayed(false)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.IN_PROGRESS)
            .build();

        orderDto = OrderDto.builder()
            .orderId(100)
            .orderDesc("Test Order")
            .orderFee(250.0)
            .build();

        paymentDto1 = PaymentDto.builder()
            .paymentId(1)
            .isPayed(true)
            .paymentStatus(com.selimhorri.app.domain.PaymentStatus.COMPLETED)
            .orderDto(orderDto)
            .build();
    }

    @Test
    void testFindAll_ReturnsListOfPaymentDto() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/100", OrderDto.class))
            .thenReturn(orderDto);
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/101", OrderDto.class))
            .thenReturn(OrderDto.builder().orderId(101).orderDesc("Order 101").orderFee(100.0).build());

        List<PaymentDto> result = paymentService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getOrderDto().getOrderId());
        verify(paymentRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void testFindById_ExistingId_ReturnsPaymentDto() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment1));
        when(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/100", OrderDto.class))
            .thenReturn(orderDto);

        PaymentDto result = paymentService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertEquals(100, result.getOrderDto().getOrderId());
        verify(paymentRepository).findById(1);
        verify(restTemplate).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void testFindById_NonExistingId_ThrowsException() {
        when(paymentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(99));
        verify(paymentRepository).findById(99);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testSave_PaymentDto_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment1);

        PaymentDto saved = paymentService.save(paymentDto1);

        assertNotNull(saved);
        assertEquals(paymentDto1.getPaymentId(), saved.getPaymentId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testDeleteById_VerifyDeletion() {
        doNothing().when(paymentRepository).deleteById(1);

        paymentService.deleteById(1);

        verify(paymentRepository).deleteById(1);
    }

}
