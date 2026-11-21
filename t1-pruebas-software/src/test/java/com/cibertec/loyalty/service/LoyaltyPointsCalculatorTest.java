package com.cibertec.loyalty.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoyaltyPointsCalculatorTest {

    private LoyaltyPointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoyaltyPointsCalculator();
    }

    @Test
    @DisplayName("Debe lanzar exception cuando el monto es menor o igual a cero")
    void calculateAccumulatedPoints_cuandoMontoEsMenorOIgualACero_debeLanzarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateAccumulatedPoints(BigDecimal.ZERO, "REGULAR"));

        assertEquals("El monto de compra debe ser mayor a cero", exception.getMessage());
    }

    @Test
    @DisplayName("el cliente regular debe asignar 1 punto por cada unidad entera del monto")
    void calculateAccumulatedPoints_cuandoClienteEsRegularAsignaUnPuntoPorCadaUnidadEntera() {
        int puntos = calculator.calculateAccumulatedPoints(new BigDecimal("150.75"), "REGULAR");

        assertEquals(150, puntos);
    }

    @Test
    @DisplayName("el client vip debe duplicar el monto y redondear")
    void calculateAccumulatedPoints_cuandoClienteEsVip_debeDuplicarMontoYRedondear() {
        // 150.75 * 2 = 301.50 -> 301 puntos
        int puntos = calculator.calculateAccumulatedPoints(new BigDecimal("150.75"), "VIP");

        assertEquals(301, puntos);
    }

}
