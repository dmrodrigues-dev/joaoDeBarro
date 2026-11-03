
package service;

import enums.StatusChamado;
import model.ChamadoManutencao;
import repository.ManutencaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManutencaoServiceTest {

    // JUnit cria uma pasta temporária para os testes.
    @TempDir
    Path pastaTemporaria;

    private ManutencaoService service;
    private Path arquivoCsv;

    /*
     * Executado antes de cada teste.
     *
     * Cria um arquivo separado para que
     * os testes não interfiram uns nos outros.
     */
    @BeforeEach
    void preparar() {
        arquivoCsv = pastaTemporaria.resolve(
                "manutencoes-teste.csv"
        );

        ManutencaoRepository repository =
                new ManutencaoRepository(arquivoCsv);

        service = new ManutencaoService(repository);
    }

    /*
     * TESTE 1
     * Verifica se um chamado é cadastrado
     * com ID e status inicial corretos.
     */
    @Test
    void deveCadastrarChamado() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Elevador",
                        "Elevador com defeito"
                );

        assertNotNull(chamado.getId());

        assertEquals(
                StatusChamado.ABERTO,
                chamado.getStatus()
        );

        assertEquals(1, service.listarTodos().size());
    }

    /*
     * TESTE 2
     * Verifica se o atendimento pode
     * ser iniciado corretamente.
     */
    @Test
    void deveIniciarAtendimento() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Piscina",
                        "Bomba com defeito"
                );

        service.iniciarAtendimento(chamado.getId());

        assertEquals(
                StatusChamado.EM_ATENDIMENTO,
                service.buscarPorId(
                        chamado.getId()
                ).getStatus()
        );
    }

    /*
     * TESTE 3
     * Verifica se o chamado é concluído
     * e se o custo é registrado.
     */
    @Test
    void deveConcluirChamado() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Portão",
                        "Motor com defeito"
                );

        service.iniciarAtendimento(chamado.getId());

        service.concluirChamado(
                chamado.getId(),
                new BigDecimal("250.00")
        );

        ChamadoManutencao atualizado =
                service.buscarPorId(chamado.getId());

        assertEquals(
                StatusChamado.CONCLUIDO,
                atualizado.getStatus()
        );

        assertEquals(
                0,
                new BigDecimal("250.00")
                        .compareTo(atualizado.getCusto())
        );

        assertNotNull(atualizado.getDataConclusao());
    }

    /*
     * TESTE 4
     * Verifica se um chamado aberto
     * não pode ser concluído diretamente.
     */
    @Test
    void naoDeveConcluirChamadoAberto() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Garagem",
                        "Lâmpada queimada"
                );

        assertThrows(
                IllegalStateException.class,
                () -> service.concluirChamado(
                        chamado.getId(),
                        new BigDecimal("50.00")
                )
        );
    }

    /*
     * TESTE 5
     * Verifica se valores negativos
     * são rejeitados na conclusão.
     */
    @Test
    void naoDeveAceitarCustoNegativo() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Jardim",
                        "Irrigação danificada"
                );

        service.iniciarAtendimento(chamado.getId());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.concluirChamado(
                        chamado.getId(),
                        new BigDecimal("-100.00")
                )
        );
    }

    /*
     * TESTE 6
     * Verifica o histórico de manutenção
     * filtrando os chamados por área.
     */
    @Test
    void deveConsultarHistoricoPorArea() {

        service.cadastrar("Elevador", "Falha no motor");
        service.cadastrar("Piscina", "Problema na bomba");
        service.cadastrar("Elevador", "Porta travada");

        List<ChamadoManutencao> historico =
                service.historicoPorArea("elevador");

        assertEquals(2, historico.size());
    }

    /*
     * TESTE 7
     * Verifica se o total de gastos
     * corresponde aos chamados concluídos.
     */
    @Test
    void deveCalcularTotalDeGastos() {

        ChamadoManutencao primeiro =
                service.cadastrar("Elevador", "Motor");

        ChamadoManutencao segundo =
                service.cadastrar("Piscina", "Bomba");

        service.iniciarAtendimento(primeiro.getId());
        service.concluirChamado(
                primeiro.getId(),
                new BigDecimal("350.00")
        );

        service.iniciarAtendimento(segundo.getId());
        service.concluirChamado(
                segundo.getId(),
                new BigDecimal("150.00")
        );

        assertEquals(
                0,
                new BigDecimal("500.00").compareTo(
                        service.calcularTotalGastos()
                )
        );
    }

    /*
     * TESTE 8
     * Verifica se os dados continuam salvos
     * após criar um novo repositório.
     */
    @Test
    void deveRecuperarChamadoDoCsv() {

        ChamadoManutencao chamado =
                service.cadastrar(
                        "Elevador",
                        "Problema elétrico"
                );

        service.iniciarAtendimento(chamado.getId());

        service.concluirChamado(
                chamado.getId(),
                new BigDecimal("350.00")
        );

        // Simula a reabertura do armazenamento.
        ManutencaoRepository novoRepository =
                new ManutencaoRepository(arquivoCsv);

        ChamadoManutencao recuperado =
                novoRepository.buscarPorId(
                        chamado.getId()
                );

        assertNotNull(recuperado);

        assertEquals(
                StatusChamado.CONCLUIDO,
                recuperado.getStatus()
        );

        assertEquals(
                0,
                new BigDecimal("350.00")
                        .compareTo(recuperado.getCusto())
        );
    }
}
