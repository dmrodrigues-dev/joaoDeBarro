package service;

import model.Morador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoradorServiceTest extends ServiceTestSupport {
    @Test
    public void cadastraMoradorVinculadoAApartamento() {
        ApartamentoService aps = new ApartamentoService(apartamentos);
        aps.cadastrarApartamento("12", "C");
        Long idApartamento = aps.buscarPorNumeroTorre("12", "C").getId();
        MoradorService service = new MoradorService(apartamentos, moradores);
        service.cadastrarMorador("Ana", "111", idApartamento, 1, 2);
        Morador m = service.buscarPorCpf("111");
        assertNotNull(m);
        assertEquals("Ana", m.getNome());
        assertEquals(idApartamento, m.getApartamentoId());
        assertEquals(1, m.getQtdPets());
        assertEquals(2, m.getVagasGaragem());
    }

    @Test
    public void rejeitaCpfDuplicado() {
        ApartamentoService aps = new ApartamentoService(apartamentos);
        aps.cadastrarApartamento("12", "C");
        Long id = aps.buscarPorNumeroTorre("12", "C").getId();
        MoradorService service = new MoradorService(apartamentos, moradores);
        service.cadastrarMorador("Ana", "111", id, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrarMorador("Outra Ana", "111", id, 0, 1);
        });
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    public void rejeitaApartamentoInexistente() {
        MoradorService service = new MoradorService(apartamentos, moradores);
        assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrarMorador("Ana", "111", 99L, 0, 1);
        });
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    public void buscarMoradorInexistente() {
        MoradorService service = new MoradorService(apartamentos, moradores);
        assertThrows(IllegalArgumentException.class, () -> {
            service.buscarPorCpf("11234");
        });
    }

    @Test
    public void removeMoradorCadastrado() {
        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long id = apartamentos.buscarPorTorreNumero("12", "C").getId();
        MoradorService service = new MoradorService(apartamentos, moradores);
        service.cadastrarMorador("Ana", "111", id, 0, 1);
        service.removerMorador("111");
        assertTrue(service.listarTodos().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> {
            service.buscarPorCpf("111");
        });
    }

    @Test
    public void removeMoradorInexistente() {
        MoradorService service = new MoradorService(apartamentos, moradores);
        assertThrows(IllegalArgumentException.class, () -> {
            service.removerMorador("111");
        });
        assertTrue(service.listarTodos().isEmpty());
    }
}
