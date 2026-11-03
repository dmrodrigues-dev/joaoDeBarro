
package service;

// Classe que representa os chamados.
import model.ChamadoManutencao;

// Repositório utilizado para salvar os dados.
import repository.ManutencaoRepository;

// Enum com os estados possíveis.
import enums.StatusChamado;

// Tipo utilizado para valores monetários.
import java.math.BigDecimal;

// Estrutura utilizada para retornar listas.
import java.util.List;

// Utilizado para filtrar valores nulos.
import java.util.Objects;

/*
 * Classe de serviço responsável por
 * controlar as operações de manutenção.
 *
 * Faz a ligação entre as regras de negócio
 * e o armazenamento dos dados.
 */
public class ManutencaoService {

    // Repositório utilizado pelo serviço.
    private final ManutencaoRepository repository;

    /*
     * Construtor.
     *
     * Inicializa o repositório responsável
     * pelos chamados de manutenção.
     */
    public ManutencaoService() {
        this.repository = new ManutencaoRepository();
    }


    /*
     * Construtor utilizado nos testes automatizados.
     *
     * Permite receber um repositório específico,
     * evitando alterar os dados reais do sistema.
     */
    public ManutencaoService(
            ManutencaoRepository repository) {

        // Impede a utilização de um repositório nulo.
        this.repository = java.util.Objects.requireNonNull(
                repository,
                "O repositorio nao pode ser nulo."
        );
    }


    /*
     * Cadastra um novo chamado.
     *
     * Recebe a área e a descrição,
     * cria o objeto e salva no CSV.
     */
    public ChamadoManutencao cadastrar(
            String area, String descricao) {

        // Cria um chamado com ID ainda não definido.
        ChamadoManutencao chamado =
                new ChamadoManutencao(
                        null, area, descricao
                );

        // O repositório atribui o ID e salva.
        repository.salvar(chamado);

        // Retorna o chamado cadastrado.
        return chamado;
    }

    /*
     * Lista todos os chamados cadastrados.
     */
    public List<ChamadoManutencao> listarTodos() {

        // Retorna os registros do repositório.
        return repository.listarTudo();
    }

    /*
     * Busca um chamado pelo identificador.
     */
    public ChamadoManutencao buscarPorId(Long id) {

        // Impede consultas com ID nulo.
        if (id == null) {
            throw new IllegalArgumentException(
                    "ID invalido."
            );
        }

        // Procura o chamado no repositório.
        ChamadoManutencao chamado =
                repository.buscarPorId(id);

        // Verifica se o registro foi encontrado.
        if (chamado == null) {
            throw new IllegalArgumentException(
                    "Chamado nao encontrado."
            );
        }

        // Retorna o chamado encontrado.
        return chamado;
    }

    /*
     * Inicia o atendimento de um chamado.
     */
    public void iniciarAtendimento(Long id) {

        // Localiza o chamado.
        ChamadoManutencao chamado = buscarPorId(id);

        // Altera seu status para EM_ATENDIMENTO.
        chamado.iniciarAtendimento();

        // Persiste a alteração no repositório.
        repository.atualizar(chamado);
    }

    /*
     * Conclui um chamado e registra seu custo.
     */
    public void concluirChamado(
            Long id, BigDecimal custo) {

        // Localiza o chamado pelo ID.
        ChamadoManutencao chamado = buscarPorId(id);

        // Aplica as regras de conclusão.
        chamado.concluir(custo);

        // Salva o estado atualizado.
        repository.atualizar(chamado);
    }

    /*
     * Consulta o histórico de chamados
     * de uma área específica.
     */
    public List<ChamadoManutencao> historicoPorArea(
            String area) {

        // Exige uma área válida.
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(
                    "Area invalida."
            );
        }

        // Percorre os registros e seleciona
        // apenas os que pertencem à área informada.
        return repository.listarTudo()
                .stream()
                .filter(c -> c.getArea() != null)
                .filter(c -> c.getArea()
                        .equalsIgnoreCase(area))
                .toList();
    }

    /*
     * Calcula o total gasto em manutenções.
     *
     * Considera somente chamados concluídos.
     */
    public BigDecimal calcularTotalGastos() {

        return repository.listarTudo()

                // Converte a lista em fluxo de dados.
                .stream()

                // Seleciona apenas chamados concluídos.
                .filter(c -> c.getStatus()
                        == StatusChamado.CONCLUIDO)

                // Obtém o custo de cada chamado.
                .map(ChamadoManutencao::getCusto)

                // Descarta custos nulos.
                .filter(Objects::nonNull)

                // Soma os valores, começando em zero.
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}
