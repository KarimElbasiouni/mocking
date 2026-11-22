package com.karim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private StripePaymentProcessor paymentProcessor;
    private OrderRepository orderRepository;
    private OrderService underTest;

    @BeforeEach
    void setUp(){

        paymentProcessor = mock();
        orderRepository = mock();
        underTest = new OrderService(paymentProcessor, orderRepository);
    }

    @Test
    void canCharge(){
        // given
        BigDecimal amount = BigDecimal.TEN;
        when(paymentProcessor.charge(any())).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(1);
        //when
        boolean actual = underTest.processOrder(null, amount);
        // then
        verify(paymentProcessor).charge(amount);
        assertThat(actual).isTrue();
    }

    @Test
    void shouldThrowWhenChargeFails(){
        // given
        BigDecimal amount = BigDecimal.TEN;
        when(paymentProcessor.charge(any())).thenReturn(false);

        //when
        assertThatThrownBy(() ->  underTest.processOrder(null, amount))
                .hasMessageContaining("Payment failed")
                .isInstanceOf(IllegalStateException.class);

        // then
        verify(paymentProcessor).charge(amount);
        verifyNoInteractions(orderRepository);
    }


}