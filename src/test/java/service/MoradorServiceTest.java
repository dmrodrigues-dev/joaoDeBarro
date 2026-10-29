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
    public void rejeitaCpfDuplicadoEApartamentoInexistente() {
        ApartamentoService aps = new ApartamentoService(apartamentos);
        aps.cadastrarApartamento("12", "C");
        Long id = aps.buscarPorNumeroTorre("12", "C").getId();
        MoradorService service = new MoradorService(apartamentos, moradores);
        service.cadastrarMorador("Ana", "111", id, 0, 1);
        service.cadastrarMorador("Outra Ana", "111", id, 0, 1);
        service.cadastrarMorador("João", "222", 999L, 0, 1);
        assertEquals(1, service.listarTodos().size());
        assertNull(service.buscarPorCpf("222"));
    }

    @Test
    public void removeMoradorCadastrado() {
        new ApartamentoService(apartamentos).cadastrarApartamento("12", "C");
        Long id = apartamentos.buscarPorTorreNumero("12", "C").getId();
        MoradorService service = new MoradorService(apartamentos, moradores);
        service.cadastrarMorador("Ana", "111", id, 0, 1);
        service.removerMorador("111");
        assertNull(service.buscarPorCpf("111"));
    }
}
