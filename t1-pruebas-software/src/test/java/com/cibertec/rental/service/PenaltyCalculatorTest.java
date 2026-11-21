package com.cibertec.rental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PenaltyCalculatorTest {

    private PenaltyCalculator penaltyCalculator;

    @BeforeEach
    void setUp() {
        penaltyCalculator = new PenaltyCalculator();
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando los días de retraso son negativos")
    void calculateLatePenalty_cuandoDiasDeRetrasoSonNegativos_debeLanzarIllegalStateException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> penaltyCalculator.calculateLatePenalty(-3));

        assertEquals("Los días de retraso no pueden ser negativos", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar penalidad 0 cuando la devolución es a tiempo")
    void calculateLatePenalty_cuandoDevolucionEsATiempo_debeRetornarCero() {
        int penalidad = penaltyCalculator.calculateLatePenalty(0);

        assertEquals(0, penalidad);
    }

    @Test
    @DisplayName("Retraso de 5 días, debe cobrar solo la tarifa base de $15 por día")
    void calculateLatePenalty_cuandoRetrasoEsLeve_debeCobrarSoloTarifaBase() {
        // 5 * 15 = 75
        int penalidad = penaltyCalculator.calculateLatePenalty(5);

        assertEquals(75, penalidad);
    }

    @Test
    @DisplayName("Retraso de 20 días, la penalidad debe limitarse al tope de 300")
    void calculateLatePenalty_cuandoRetrasoEsProlongado_debeAplicarTopeMaximoDeTrescientos() {
        int penalidad = penaltyCalculator.calculateLatePenalty(20);

        assertEquals(300, penalidad);
    }

}
