package com.karim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.matchers.Or;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private StripePaymentProcessor paymentProcessor;
    private OrderRepository orderRepository;
    private OrderService underTest;

    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);

    @BeforeEach
    void setUp(){

        paymentProcessor = mock();
        orderRepository = mock();
        underTest = new OrderService(paymentProcessor, orderRepository);
    }

    @Test
    void canChargeWithAssertArg(){
        // given
        BigDecimal amount = BigDecimal.TEN;
        User user = new User(1, "James");
        when(paymentProcessor.charge(any())).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(1);

        //when
        boolean actual = underTest.processOrder(user, amount);
        // then
        verify(paymentProcessor).charge(amount);
        verify(orderRepository).save(assertArg(order -> {
            assertThat(order.amount()).isEqualTo(amount);
            assertThat(order.user()).isEqualTo(user);
            assertThat(order.id()).isNotNull();
            assertThat(order.zonedDateTime())
                    .isBefore(ZonedDateTime.now())
                    .isNotNull();
        }));
        assertThat(actual).isTrue();
    }

    @Test
    void canChargeWithArgCaptor(){
        // given
        BigDecimal amount = BigDecimal.TEN;
        User user = new User(1, "James");
        when(paymentProcessor.charge(any())).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(1);

        //when
        boolean actual = underTest.processOrder(user, amount);
        // then
        InOrder inOrder = inOrder(paymentProcessor, orderRepository);
        inOrder.verify(paymentProcessor).charge(amount);
        inOrder.verify(orderRepository).save(orderArgumentCaptor.capture());

        Order orderArgumentCaptorValue = orderArgumentCaptor.getValue();
        assertThat(orderArgumentCaptorValue.amount()).isEqualTo(amount);
        assertThat(orderArgumentCaptorValue.user()).isEqualTo(user);
        assertThat(orderArgumentCaptorValue.id()).isNotNull();
        assertThat(orderArgumentCaptorValue.zonedDateTime())
                .isBefore(ZonedDateTime.now())
                .isNotNull();

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

    @Test
    void shouldThrowWhenChargeFailsWithMockitoBDD(){
        // given
        BigDecimal amount = BigDecimal.TEN;
        given(paymentProcessor.charge(any())).willReturn(false);

        //when
        assertThatThrownBy(() ->  underTest.processOrder(null, amount))
                .hasMessageContaining("Payment failed")
                .isInstanceOf(IllegalStateException.class);

        // then
       then(paymentProcessor).should().charge(amount);
       then(orderRepository).shouldHaveNoInteractions();
    }


}