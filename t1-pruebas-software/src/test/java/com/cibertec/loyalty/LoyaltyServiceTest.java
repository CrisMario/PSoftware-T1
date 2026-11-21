package com.cibertec.loyalty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException y NO registrar puntos cuando el monto es menor o igual a cero")
    void calculateAndAssignPoints_cuandoMontoEsInvalido_debeLanzarExcepcionYNoRegistrarPuntos() {
        Long userId = 1L;

        assertThatThrownBy(() -> loyaltyService.calculateAndAssignPoints(userId, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a cero");

        verify(loyaltyRepository, never()).addPoints(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Cliente vip tiene que otorgar el doble de puntos y registrar el puntaje duplicado exacto")
    void calculateAndAssignPoints_cuandoClienteEsVip_debeDuplicarPuntosYRegistrarlosEnRepositorio() {
        Long userId = 10L;
        BigDecimal montoCompra = new BigDecimal("150.75");
        int puntosEsperados = 300;

        when(loyaltyRepository.isVipUser(userId)).thenReturn(true);

        LoyaltyResponse response = loyaltyService.calculateAndAssignPoints(userId, montoCompra);

        assertThat(response.isVip()).isTrue();
        assertThat(response.pointsEarned()).isEqualTo(puntosEsperados);

        verify(loyaltyRepository, times(1)).addPoints(userId, puntosEsperados);
    }

}
