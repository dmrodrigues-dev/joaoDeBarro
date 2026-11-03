
package model;

// Biblioteca utilizada para mapear campos
// da classe para colunas de arquivos CSV.
import com.opencsv.bean.CsvBindByName;

// Importa os estados possíveis do chamado.
import enums.StatusChamado;

// Utilizado para representar valores monetários.
import java.math.BigDecimal;

// Utilizado para trabalhar com datas.
import java.time.LocalDate;

/*
 * Representa um chamado de manutenção.
 *
 * Implementa Identificavel para que o
 * repositório genérico consiga trabalhar
 * com o identificador do chamado.
 */
public class ChamadoManutencao implements Identificavel {

    // Identificador único do chamado.
    // Long permite trabalhar com o padrão
    // de IDs utilizado pelo repositório.
    @CsvBindByName
    private Long id;

    // Área do condomínio que precisa
    // de manutenção, como elevador ou piscina.
    @CsvBindByName
    private String area;

    // Descrição do problema informado.
    @CsvBindByName
    private String descricao;

    // Data em que o chamado foi criado.
    // Armazenada como texto para facilitar
    // a leitura e gravação no CSV.
    @CsvBindByName
    private String dataAbertura;

    // Data de conclusão do serviço.
    // Permanece nula enquanto não for concluído.
    @CsvBindByName
    private String dataConclusao;

    // Estado atual do chamado.
    // Armazenado como texto no arquivo CSV.
    @CsvBindByName
    private String status;

    // Valor gasto na manutenção.
    @CsvBindByName
    private BigDecimal custo;

    /*
     * Construtor vazio.
     *
     * Necessário para que o OpenCSV consiga
     * criar objetos ao carregar os registros.
     */
    public ChamadoManutencao() {
    }

    /*
     * Construtor utilizado para cadastrar
     * um novo chamado de manutenção.
     */
    public ChamadoManutencao(
            Long id, String area, String descricao) {

        // Impede o cadastro sem informar a área.
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(
                    "A area deve ser informada."
            );
        }

        // Impede o cadastro sem descrição.
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descricao deve ser informada."
            );
        }

        // Inicializa os dados recebidos.
        this.id = id;
        this.area = area;
        this.descricao = descricao;

        // Registra automaticamente a data atual.
        this.dataAbertura = LocalDate.now().toString();

        // O chamado ainda não foi concluído.
        this.dataConclusao = null;

        // Todo chamado começa com status ABERTO.
        this.status = StatusChamado.ABERTO.name();

        // O custo inicial é zero.
        this.custo = BigDecimal.ZERO;
    }

    // Retorna o identificador do chamado.
    @Override
    public Long getId() {
        return id;
    }

    // Define o identificador do chamado.
    // Utilizado pelo repositório.
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    // Retorna a área do condomínio.
    public String getArea() {
        return area;
    }

    // Retorna a descrição do problema.
    public String getDescricao() {
        return descricao;
    }

    /*
     * Converte a data armazenada como texto
     * para um objeto LocalDate.
     */
    public LocalDate getDataAbertura() {
        return LocalDate.parse(dataAbertura);
    }

    /*
     * Retorna a data de conclusão.
     *
     * Caso o chamado não tenha sido concluído,
     * retorna null.
     */
    public LocalDate getDataConclusao() {

        if (dataConclusao == null ||
                dataConclusao.isBlank()) {
            return null;
        }

        return LocalDate.parse(dataConclusao);
    }

    /*
     * Converte o texto armazenado no CSV
     * para um valor do enum StatusChamado.
     */
    public StatusChamado getStatus() {
        return StatusChamado.valueOf(status);
    }

    // Retorna o custo registrado.
    public BigDecimal getCusto() {
        return custo;
    }

    /*
     * Inicia o atendimento.
     *
     * Somente chamados ABERTOS podem
     * passar para EM_ATENDIMENTO.
     */
    public void iniciarAtendimento() {

        // Verifica se a mudança é permitida.
        if (getStatus() != StatusChamado.ABERTO) {
            throw new IllegalStateException(
                    "O chamado nao esta aberto."
            );
        }

        // Atualiza o estado do chamado.
        this.status =
                StatusChamado.EM_ATENDIMENTO.name();
    }

    /*
     * Conclui o chamado de manutenção.
     *
     * Registra o custo e a data de conclusão.
     */
    public void concluir(BigDecimal custo) {

        // Exige que o atendimento tenha iniciado.
        if (getStatus() !=
                StatusChamado.EM_ATENDIMENTO) {
            throw new IllegalStateException(
                    "O chamado nao esta em atendimento."
            );
        }

        // Impede valores nulos ou negativos.
        if (custo == null || custo.signum() < 0) {
            throw new IllegalArgumentException(
                    "O custo nao pode ser negativo."
            );
        }

        // Armazena o valor gasto.
        this.custo = custo;

        // Altera o estado para concluído.
        this.status = StatusChamado.CONCLUIDO.name();

        // Registra a data da finalização.
        this.dataConclusao = LocalDate.now().toString();
    }
}
