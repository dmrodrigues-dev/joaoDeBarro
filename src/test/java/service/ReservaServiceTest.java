
package service;

import enums.StatusCobranca;
import enums.StatusReserva;
import enums.TipoCobranca;
import model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaServiceTest extends ServiceTestSupport {

    private Morador morador() {
        Morador morador = new Morador("Ana", "111", 1L, 0, 1);
        morador.setId(1L);
        return morador;
    }

    private ReservaService service() {
        return new ReservaService(reservas, cobrancas);
    }

    @Test
    public void reservaAreaDentroDoLimiteECriaCobranca() {
        service().reservarAreaComum(
                morador(),
                new Churrasqueira(1L),
                4,
                LocalTime.of(10, 0),
                LocalDate.now().plusDays(5)
        );

        assertEquals(1, reservas.listarTudo().size());
        assertEquals(StatusReserva.RESERVADO,
                reservas.listarTudo().get(0).getStatus());

        assertEquals(1, cobrancas.listarTudo().size());
        assertEquals(0, new BigDecimal("80").compareTo(
                cobrancas.listarTudo().get(0).getValor()));
        assertEquals(TipoCobranca.TAXA_RESERVA,
                cobrancas.listarTudo().get(0).getTipo());
    }

    @Test
    public void rejeitaHorasInvalidas() {
        LocalDate dia = LocalDate.now().plusDays(5);

        assertThrows(IllegalArgumentException.class, () ->
                service().reservarAreaComum(
                        morador(), new Churrasqueira(1L),
                        0, LocalTime.NOON, dia
                ));

        assertThrows(IllegalArgumentException.class, () ->
                service().reservarAreaComum(
                        morador(), new Churrasqueira(1L),
                        5, LocalTime.NOON, dia
                ));

        assertTrue(reservas.listarTudo().isEmpty());
        assertTrue(cobrancas.listarTudo().isEmpty());
    }

    @Test
    public void rejeitaConflitoMasPermiteHorariosAdjacentes() {
        ReservaService service = service();
        LocalDate dia = LocalDate.now().plusDays(5);
        Churrasqueira area = new Churrasqueira(1L);

        service.reservarAreaComum(
                morador(), area, 2, LocalTime.of(10, 0), dia
        );

        assertThrows(IllegalStateException.class, () ->
                service.reservarAreaComum(
                        morador(), area, 1,
                        LocalTime.of(11, 0), dia
                ));

        service.reservarAreaComum(
                morador(), area, 1, LocalTime.of(12, 0), dia
        );

        assertEquals(2, reservas.listarTudo().size());
    }

    @Test
    public void bloqueiaAtrasoSuperiorATrintaDias() {
        LocalDate dia = LocalDate.now().plusDays(5);

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                dia.minusDays(31),
                new BigDecimal("500"),
                1L
        );
        cobrancas.salvar(cobranca);

        assertThrows(IllegalStateException.class, () ->
                service().reservarAreaComum(
                        morador(), new Academia(2L),
                        1, LocalTime.of(10, 0), dia
                ));

        assertTrue(reservas.listarTudo().isEmpty());
    }

    @Test
    public void permiteAtrasoDeExatosTrintaDias() {
        LocalDate dia = LocalDate.now().plusDays(5);

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                dia.minusDays(30),
                new BigDecimal("500"),
                1L
        );
        cobrancas.salvar(cobranca);

        service().reservarAreaComum(
                morador(), new Academia(2L),
                1, LocalTime.of(10, 0), dia
        );

        assertEquals(1, reservas.listarTudo().size());
    }

    @Test
    public void naoBloqueiaPorCobrancaPaga() {
        LocalDate dia = LocalDate.now().plusDays(5);

        Cobranca cobranca = new Cobranca(
                null,
                TipoCobranca.TAXA_CONDOMINIAL,
                1L,
                dia.minusDays(40),
                new BigDecimal("500"),
                1L
        );

        cobranca.setStatus(StatusCobranca.PAGO);
        cobrancas.salvar(cobranca);

        service().reservarAreaComum(
                morador(), new Academia(2L),
                1, LocalTime.of(10, 0), dia
        );

        assertEquals(1, reservas.listarTudo().size());
    }

    @Test
    public void cancelamentoAntecipadoNaoGeraMulta() {
        ReservaService service = service();

        service.reservarAreaComum(
                morador(), new Churrasqueira(1L),
                2, LocalTime.of(10, 0),
                LocalDate.now().plusDays(5)
        );

        Long id = reservas.listarTudo().get(0).getId();

        service.cancelarReserva(id);

        assertEquals(StatusReserva.CANCELADO,
                reservas.buscarPorId(id).getStatus());

        assertEquals(1, cobrancas.listarTudo().size());
        assertEquals(StatusCobranca.CANCELADO,
                cobrancas.buscarTaxaReserva(id).getStatus());
    }

    @Test
    public void cancelamentoTardioGeraMultaDeCinquentaPorCento() {
        ReservaService service = service();

        LocalDateTime inicio = LocalDateTime.now()
                .plusHours(24)
                .withSecond(0)
                .withNano(0);

        service.reservarAreaComum(
                morador(), new Churrasqueira(1L),
                2, inicio.toLocalTime(), inicio.toLocalDate()
        );

        Long id = reservas.listarTudo().get(0).getId();

        service.cancelarReserva(id);

        assertEquals(StatusReserva.CANCELADO,
                reservas.buscarPorId(id).getStatus());

        assertEquals(2, cobrancas.listarTudo().size());

        Cobranca multa = cobrancas.buscarPorReserva(id)
                .stream()
                .filter(c -> c.getTipo()
                        == TipoCobranca.CANCELAMENTO_RESERVA)
                .findFirst()
                .orElseThrow();

        assertEquals(0, new BigDecimal("40").compareTo(
                multa.getValor()));
    }

    @Test
    public void areaGratuitaNaoGeraMulta() {
        ReservaService service = service();

        LocalDateTime inicio = LocalDateTime.now()
                .plusHours(24)
                .withSecond(0)
                .withNano(0);

        service.reservarAreaComum(
                morador(), new Piscina(3L),
                1, inicio.toLocalTime(), inicio.toLocalDate()
        );

        Long id = reservas.listarTudo().get(0).getId();

        service.cancelarReserva(id);

        assertEquals(StatusReserva.CANCELADO,
                reservas.buscarPorId(id).getStatus());

        assertTrue(cobrancas.listarTudo().isEmpty());
    }

    @Test
    public void naoPermiteCancelarDuasVezes() {
        ReservaService service = service();

        service.reservarAreaComum(
                morador(), new Academia(2L),
                1, LocalTime.of(10, 0),
                LocalDate.now().plusDays(5)
        );

        Long id = reservas.listarTudo().get(0).getId();

        service.cancelarReserva(id);

        assertThrows(IllegalStateException.class,
                () -> service.cancelarReserva(id));
    }

    @Test
    public void concluiReserva() {
        ReservaService service = service();

        service.reservarAreaComum(
                morador(), new Academia(2L),
                1, LocalTime.of(10, 0),
                LocalDate.now().plusDays(5)
        );

        Long id = reservas.listarTudo().get(0).getId();

        service.concluirReserva(id);

        assertEquals(StatusReserva.CONCLUIDO,
                reservas.buscarPorId(id).getStatus());
    }

    @Test
    public void naoPermiteReservaNoPassado() {
        ReservaService service = service();

        assertThrows(IllegalArgumentException.class, () ->
                service.reservarAreaComum(
                        morador(),
                        new Churrasqueira(1L),
                        2,
                        LocalTime.of(10, 0),
                        LocalDate.now().minusDays(1)
                )
        );

        assertTrue(reservas.listarTudo().isEmpty());
    }

    @Test
    public void naoPermiteReservaAposMeiaNoite() {
        ReservaService service = service();

        assertThrows(IllegalArgumentException.class, () ->
                service.reservarAreaComum(
                        morador(),
                        new Churrasqueira(1L),
                        3,
                        LocalTime.of(23, 0),
                        LocalDate.now().plusDays(5)
                )
        );

        assertTrue(reservas.listarTudo().isEmpty());
    }

}
