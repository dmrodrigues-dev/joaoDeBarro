
package view;

// Importa a classe que representa um chamado.
import model.ChamadoManutencao;

// Importa o serviço com as regras de manutenção.
import service.ManutencaoService;

// Utilizado para trabalhar com valores monetários.
import java.math.BigDecimal;

// Utilizado para ler informações digitadas.
import java.util.Scanner;

// Utilizado para trabalhar com listas.
import java.util.List;

/*
 * Menu responsável pela interação do usuário
 * com o módulo de manutenção.
 *
 * Não contém regras de negócio: essas regras
 * são executadas pelo ManutencaoService.
 */
public class MenuManutencao {

    // Serviço que executa as operações.
    private final ManutencaoService service;

    // Scanner utilizado para ler o teclado.
    private final Scanner scanner;

    /*
     * Construtor do menu.
     *
     * Inicializa o serviço e o leitor de dados.
     */
    public MenuManutencao() {
        this.service = new ManutencaoService();
        this.scanner = new Scanner(System.in);
    }

    /*
     * Método principal do menu.
     *
     * Exibe as opções repetidamente até
     * o usuário escolher a opção zero.
     */
    public void exibir() {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println(
                    "\n========== MANUTENCAO =========="
            );

            System.out.println("1 - Cadastrar chamado");
            System.out.println("2 - Listar chamados");
            System.out.println("3 - Buscar chamado por ID");
            System.out.println("4 - Iniciar atendimento");
            System.out.println("5 - Concluir chamado");
            System.out.println("6 - Historico por area");
            System.out.println("7 - Total de gastos");
            System.out.println("0 - Voltar / Sair");

            System.out.print("Escolha uma opcao: ");

            try {

                // Converte o texto digitado para inteiro.
                opcao = Integer.parseInt(
                        scanner.nextLine().trim()
                );

                // Executa a operação escolhida.
                switch (opcao) {

                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> iniciar();
                    case 5 -> concluir();
                    case 6 -> historico();
                    case 7 -> totalGastos();

                    case 0 -> System.out.println(
                            "Saindo do menu de manutencao."
                    );

                    default -> System.out.println(
                            "Opcao invalida."
                    );
                }

            } catch (IllegalArgumentException |
                     IllegalStateException e) {

                // Exibe erros de entrada e de negócio
                // sem encerrar o programa.
                System.out.println(
                        "Erro: " + e.getMessage()
                );
            }
        }
    }

    /*
     * Cadastra um novo chamado de manutenção.
     */
    private void cadastrar() {

        System.out.print("Area: ");
        String area = scanner.nextLine();

        System.out.print("Descricao: ");
        String descricao = scanner.nextLine();

        // Solicita ao serviço o cadastro.
        ChamadoManutencao chamado =
                service.cadastrar(area, descricao);

        System.out.println(
                "Chamado cadastrado! ID: " +
                        chamado.getId()
        );
    }

    /*
     * Lista todos os chamados cadastrados.
     */
    private void listar() {

        List<ChamadoManutencao> chamados =
                service.listarTodos();

        if (chamados.isEmpty()) {
            System.out.println(
                    "Nenhum chamado cadastrado."
            );
            return;
        }

        for (ChamadoManutencao chamado : chamados) {
            mostrarChamado(chamado);
        }
    }

    /*
     * Busca um chamado pelo identificador.
     */
    private void buscar() {

        Long id = lerId();

        ChamadoManutencao chamado =
                service.buscarPorId(id);

        mostrarChamado(chamado);
    }

    /*
     * Inicia o atendimento de um chamado.
     */
    private void iniciar() {

        Long id = lerId();

        service.iniciarAtendimento(id);

        System.out.println(
                "Atendimento iniciado com sucesso!"
        );
    }

    /*
     * Conclui o atendimento e registra o custo.
     */
    private void concluir() {

        Long id = lerId();

        System.out.print("Custo da manutencao: R$ ");

        // BigDecimal evita problemas de precisão
        // comuns em valores monetários.
        String valor = scanner.nextLine()
                .trim()
                .replace(",", ".");

        BigDecimal custo = new BigDecimal(valor);

        service.concluirChamado(id, custo);

        System.out.println(
                "Chamado concluido com sucesso!"
        );
    }

    /*
     * Consulta o histórico de uma área.
     */
    private void historico() {

        System.out.print("Informe a area: ");
        String area = scanner.nextLine();

        List<ChamadoManutencao> chamados =
                service.historicoPorArea(area);

        if (chamados.isEmpty()) {
            System.out.println(
                    "Nenhum chamado encontrado."
            );
            return;
        }

        for (ChamadoManutencao chamado : chamados) {
            mostrarChamado(chamado);
        }
    }

    /*
     * Exibe o total gasto em manutenções
     * que já foram concluídas.
     */
    private void totalGastos() {

        BigDecimal total =
                service.calcularTotalGastos();

        System.out.println(
                "Total de gastos: R$ " + total
        );
    }

    /*
     * Lê o identificador digitado pelo usuário.
     */
    private Long lerId() {

        System.out.print("Informe o ID: ");

        return Long.parseLong(
                scanner.nextLine().trim()
        );
    }

    /*
     * Exibe os dados de um chamado.
     *
     * Esse método é reutilizado pelas opções
     * de listagem, busca e histórico.
     */
    private void mostrarChamado(
            ChamadoManutencao chamado) {

        System.out.println(
                "\n-----------------------------"
        );

        System.out.println("ID: " + chamado.getId());
        System.out.println("Area: " + chamado.getArea());

        System.out.println(
                "Descricao: " + chamado.getDescricao()
        );

        System.out.println(
                "Abertura: " + chamado.getDataAbertura()
        );

        System.out.println(
                "Status: " + chamado.getStatus()
        );

        System.out.println(
                "Conclusao: " + chamado.getDataConclusao()
        );

        System.out.println(
                "Custo: R$ " + chamado.getCusto()
        );
    }

    /*
     * Permite executar o menu separadamente
     * enquanto o Main do grupo não está pronto.
     */
    public static void main(String[] args) {

        MenuManutencao menu = new MenuManutencao();

        menu.exibir();
    }
}
