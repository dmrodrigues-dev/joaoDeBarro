package model;

import enums.StatusCobranca;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CobrancaTest {

    @Test
    public void cobrancaSemAtrasoDeveManterValorOriginal() {
        Cobranca cobranca = new Cobranca(
                null, null, 1L, LocalDate.of(2026, 10, 20),
                new BigDecimal("100.00"), 1L
        );

        assertEquals(new BigDecimal("100.00"),
                cobranca.valorComEncargos(LocalDate.of(2026, 10, 20)));
    }

    @Test
    public void cobrancaPendenteAtrasadaDeveTerDiasDeAtrasoPositivos() {
        Cobranca cobranca = new Cobranca(
                null, null, 1L, LocalDate.of(2026, 10, 10),
                new BigDecimal("100.00"), 1L
        );

        assertEquals(10, cobranca.diasAtraso(LocalDate.of(2026, 10, 20)));
    }

    @Test
    public void cobrancaAtrasadaDeveAplicarMultaEJuros() {
        Cobranca cobranca = new Cobranca(
                null, null, 1L, LocalDate.of(2026, 10, 10),
                new BigDecimal("100.00"), 1L
        );

        // R$ 100,00 + multa de 2% (R$ 2,00) + juros de 10 dias (R$ 0,33).
        assertEquals(new BigDecimal("102.33"),
                cobranca.valorComEncargos(LocalDate.of(2026, 10, 20)));
    }

    @Test
    public void cobrancaPagaNaoDeveSerConsideradaAtrasada() {
        Cobranca cobranca = new Cobranca(
                null, null, 1L, LocalDate.of(2026, 10, 10),
                new BigDecimal("100.00"), 1L
        );
        cobranca.setStatus(StatusCobranca.PAGO);

        assertFalse(cobranca.estaAtrasada(LocalDate.of(2026, 10, 20)));
        assertEquals(0, cobranca.diasAtraso(LocalDate.of(2026, 10, 20)));
    }
}
