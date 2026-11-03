
package repository;

// Importa a classe que representa os chamados.
import model.ChamadoManutencao;

// Permite trabalhar com caminhos de arquivos.
import java.nio.file.Path;

/*
 * Repositório responsável por armazenar
 * e recuperar os chamados de manutenção.
 *
 * Herda as funcionalidades de CsvRepository,
 * como salvar, listar, buscar e remover.
 */
public class ManutencaoRepository
        extends CsvRepository<ChamadoManutencao> {

    /*
     * Construtor principal.
     *
     * Define o arquivo manutencoes.csv
     * como local de armazenamento dos chamados.
     *
     * Este construtor é utilizado pelo sistema.
     */
    public ManutencaoRepository() {

        // Chama o construtor da classe pai,
        // informando o caminho e o tipo dos dados.
        super(
                Path.of("data", "manutencoes.csv"),
                ChamadoManutencao.class
        );
    }

    /*
     * Construtor utilizado nos testes.
     *
     * Permite informar um caminho diferente
     * para o arquivo CSV.
     *
     * Assim, os testes automatizados não
     * modificam os dados reais do sistema.
     */
    public ManutencaoRepository(Path caminho) {

        // Utiliza o caminho recebido.
        super(
                caminho,
                ChamadoManutencao.class
        );
    }

    /*
     * Atualiza um chamado já cadastrado.
     *
     * Primeiro verifica se o chamado existe.
     * Depois remove o registro antigo
     * e salva a versão atualizada.
     */
    public void atualizar(ChamadoManutencao chamado) {

        // Verifica se o chamado e seu ID são válidos.
        if (chamado == null ||
                chamado.getId() == null) {

            throw new IllegalArgumentException(
                    "Chamado invalido."
            );
        }

        // Verifica se o chamado existe no repositório.
        if (buscarPorId(chamado.getId()) == null) {

            throw new IllegalArgumentException(
                    "Chamado nao encontrado."
            );
        }

        // Remove o registro antigo.
        remover(chamado.getId());

        // Salva o chamado com os dados atualizados.
        salvar(chamado);
    }
}
