package com.cibertec.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    @DisplayName("Tarjeta vencida,  debe lanzar ExpiredCardException, suspender la cuenta y no cobrar")
    void renewSubscription_cuandoTarjetaEstaVencida_debeLanzarExcepcionYSuspenderCuenta() {
        Long subscriptionId = 1L;
        Subscription suscripcion = new Subscription(subscriptionId, "ACTIVE", LocalDate.of(2026, 11, 15));

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcion);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(false);

        assertThatThrownBy(() -> subscriptionService.renewSubscription(subscriptionId))
                .isInstanceOf(ExpiredCardException.class)
                .hasMessageContaining("expirado");

        verify(subscriptionRepository, times(1)).updateStatus(subscriptionId, "SUSPENDED");

        verify(paymentGatewayClient, times(1)).isCardValid(subscriptionId);
        verify(paymentGatewayClient, never()).chargeMonthlyFee(anyLong());
        verifyNoMoreInteractions(paymentGatewayClient);
    }

    @Test
    @DisplayName("Tarjeta vigente: debe cobrar y extender la membresía exactamente un mes")
    void renewSubscription_cuandoTarjetaEsValida_debeCobrarYExtenderMembresiaUnMes() {
        Long subscriptionId = 2L;
        LocalDate fechaActual = LocalDate.of(2026, 11, 15);
        Subscription suscripcion = new Subscription(subscriptionId, "ACTIVE", fechaActual);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(suscripcion);
        when(paymentGatewayClient.isCardValid(subscriptionId)).thenReturn(true);

        Subscription resultado = subscriptionService.renewSubscription(subscriptionId);

        assertThat(resultado.status()).isEqualTo("ACTIVE");

        verify(paymentGatewayClient, times(1)).chargeMonthlyFee(subscriptionId);

        ArgumentCaptor<LocalDate> fechaCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(subscriptionRepository, times(1)).updateBillingDate(eq(subscriptionId), fechaCaptor.capture());
        assertThat(fechaCaptor.getValue()).isEqualTo(fechaActual.plusMonths(1));
    }

}
