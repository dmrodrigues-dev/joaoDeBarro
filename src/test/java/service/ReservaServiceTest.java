package service;

import enums.StatusCobranca;
import enums.StatusReserva;
import enums.TipoCobranca;
import model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservaServiceTest extends ServiceTestSupport {
    private Morador morador() {
        Morador morador = new Morador("Ana", "111", 1L, 0, 1);
        morador.setId(1L);
        return morador;
    }

    @Test
    public void reservaAreaDentroDoLimiteECriaCobranca() {
        ReservaService service = new ReservaService(reservas, cobrancas);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.reservarAreaComum(morador(), new Churrasqueira(1L), 4, LocalTime.of(10, 0), dia);
        assertEquals(1, reservas.listarTudo().size());
        assertEquals(StatusReserva.RESERVADO, reservas.listarTudo().get(0).getStatus());
        assertEquals(1, cobrancas.listarTudo().size());
        assertEquals(new BigDecimal("80"), cobrancas.listarTudo().get(0).getValor());
        assertEquals(TipoCobranca.TAXA_RESERVA, cobrancas.listarTudo().get(0).getTipo());
    }

    @Test
    public void rejeitaHorasInvalidas() {
        ReservaService service = new ReservaService(reservas, cobrancas);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        service.reservarAreaComum(morador(), new Churrasqueira(1L), 0, LocalTime.NOON, dia);
        service.reservarAreaComum(morador(), new Churrasqueira(1L), 5, LocalTime.NOON, dia);
        assertTrue(reservas.listarTudo().isEmpty());
        assertTrue(cobrancas.listarTudo().isEmpty());
    }

    @Test
    public void rejeitaConflitoMasPermiteHorariosAdjacentes() {
        ReservaService service = new ReservaService(reservas, cobrancas);
        LocalDate dia = LocalDate.of(2026, 11, 1);
        Churrasqueira area = new Churrasqueira(1L);
        service.reservarAreaComum(morador(), area, 2, LocalTime.of(10, 0), dia);
        service.reservarAreaComum(morador(), area, 1, LocalTime.of(11, 0), dia);
        service.reservarAreaComum(morador(), area, 1, LocalTime.of(12, 0), dia);
        assertEquals(2, reservas.listarTudo().size());
    }

    @Test
    public void bloqueiaMoradorComCobrancaAtrasadaHaMaisDeTrintaDias() {
        Cobranca cobranca = new Cobranca(null, TipoCobranca.TAXA_CONDOMINIAL, 1L,
                LocalDate.of(2026, 9, 1), new BigDecimal("500"), 1L);
        cobrancas.salvar(cobranca);
        ReservaService service = new ReservaService(reservas, cobrancas);
        service.reservarAreaComum(morador(), new Academia(2L), 1, LocalTime.of(10, 0), LocalDate.of(2026, 10, 5));
        assertTrue(reservas.listarTudo().isEmpty());
    }

    @Test
    public void naoBloqueiaPorCobrancaPaga() {
        Cobranca cobranca = new Cobranca(null, TipoCobranca.TAXA_CONDOMINIAL, 1L,
                LocalDate.of(2026, 9, 1), new BigDecimal("500"), 1L);
        cobranca.setStatus(StatusCobranca.PAGO);
        cobrancas.salvar(cobranca);
        ReservaService service = new ReservaService(reservas, cobrancas);
        service.reservarAreaComum(morador(), new Academia(2L), 1, LocalTime.of(10, 0), LocalDate.of(2026, 10, 5));
        assertEquals(1, reservas.listarTudo().size());
    }
}
