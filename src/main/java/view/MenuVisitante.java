package view;

import model.Morador;
import model.RegistroVisita;
import model.Visitante;
import repository.AutorizacaoRepository;
import repository.MoradorRepository;
import repository.RegistroVisitaRepository;
import repository.VisitanteRepository;
import service.AutorizacaoService;
import service.ControleVisitaService;
import service.VisitanteService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/*
 * Menu do módulo de visitantes.
 *
 * Só lê e mostra dados: as regras ficam em VisitanteService,
 * AutorizacaoService e ControleVisitaService.
 */
public class MenuVisitante {

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner scanner;
    private final VisitanteService visitanteService;
    private final AutorizacaoService autorizacaoService;
    private final ControleVisitaService controleService;
    private final VisitanteRepository visitanteRepository;
    private final MoradorRepository moradorRepository;

    public MenuVisitante() {
        this.scanner = new Scanner(System.in);

        this.visitanteRepository = new VisitanteRepository();
        this.moradorRepository = new MoradorRepository();
        AutorizacaoRepository autorizacaoRepository = new AutorizacaoRepository();
        RegistroVisitaRepository registroRepository = new RegistroVisitaRepository();

        this.visitanteService = new VisitanteService(visitanteRepository);
        this.autorizacaoService = new AutorizacaoService(
                moradorRepository, visitanteRepository, autorizacaoRepository);
        this.controleService = new ControleVisitaService(
                moradorRepository, visitanteRepository, autorizacaoRepository, registroRepository);
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== VISITANTES ==========");
            System.out.println("1 - Cadastrar visitante");
            System.out.println("2 - Listar visitantes");
            System.out.println("3 - Autorizar visitante (morador + data)");
            System.out.println("4 - Consultar autorizacoes do visitante por data");
            System.out.println("5 - Registrar ENTRADA");
            System.out.println("6 - Registrar SAIDA");
            System.out.println("7 - Visitantes no condominio agora");
            System.out.println("8 - Historico por periodo");
            System.out.println("9 - Historico por visitante");
            System.out.println("10 - Historico por morador");
            System.out.println("11 - Resumo do periodo");
            System.out.println("0 - Voltar / Sair");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());

                switch (opcao) {
                    case 1 -> cadastrarVisitante();
                    case 2 -> visitanteService.listarTodos().forEach(this::mostrarVisitante);
                    case 3 -> autorizar();
                    case 4 -> consultarAutorizacoes();
                    case 5 -> registrarEntrada();
                    case 6 -> registrarSaida();
                    case 7 -> mostrarEmAndamento();
                    case 8 -> historicoPeriodo();
                    case 9 -> historicoVisitante();
                    case 10 -> historicoMorador();
                    case 11 -> resumo();
                    case 0 -> System.out.println("Saindo do menu de visitantes.");
                    default -> System.out.println("Opcao invalida.");
                }

            } catch (IllegalArgumentException | IllegalStateException | DateTimeParseException e) {
                // NumberFormatException (opcao invalida) também cai aqui.
                System.out.println("Erro: " + e.getMessage());
                if (e instanceof DateTimeParseException) {
                    System.out.println("Use a data no formato AAAA-MM-DD (ex.: 2026-11-20).");
                }
            }
        }
    }

    // ---------- acoes ----------

    private void cadastrarVisitante() {
        System.out.println("\n--- NOVO VISITANTE ---");
        String nome = ler("Nome");
        String cpf = ler("CPF");
        visitanteService.cadastrarVisitante(nome, cpf);
    }

    private void autorizar() {
        System.out.println("\n--- AUTORIZAR VISITANTE ---");
        String cpfMorador = ler("CPF do morador");
        LocalDate dia = lerData("Data da visita (AAAA-MM-DD, vazio = hoje)");
        String cpfVisitante = ler("CPF do visitante");

        // Se o visitante ainda não existe, o AutorizacaoService cadastra com este nome.
        String nome = "";
        if (visitanteRepository.buscarCpf(cpfVisitante) == null) {
            nome = ler("Nome do visitante (novo cadastro)");
        }

        autorizacaoService.cadastrarAutorizacao(cpfMorador, dia, cpfVisitante, nome);
    }

    private void consultarAutorizacoes() {
        String cpf = ler("CPF do visitante");
        LocalDate dia = lerData("Data (AAAA-MM-DD, vazio = hoje)");

        autorizacaoService.buscarPorVisitanteDia(cpf, dia).forEach(a -> {
            Morador m = moradorRepository.buscarPorId(a.getMoradorId());
            System.out.println("Autorizado por: " + (m != null ? m.getNome() : "morador " + a.getMoradorId())
                    + " | Dia: " + a.getDia());
        });
    }

    private void registrarEntrada() {
        System.out.println("\n--- ENTRADA ---");
        String cpfVisitante = ler("CPF do visitante");

        List<Morador> autorizadores = controleService.moradoresAutorizadosHoje(cpfVisitante);

        RegistroVisita registro;
        if (autorizadores.size() > 1) {
            System.out.println("Autorizado por mais de um morador hoje:");
            autorizadores.forEach(m -> System.out.println("  " + m.getNome() + " | CPF " + m.getCpf()));
            registro = controleService.registrarEntrada(cpfVisitante, ler("CPF do morador que vai receber"));
        } else {
            registro = controleService.registrarEntrada(cpfVisitante);
        }

        System.out.println("Entrada registrada as " + registro.getEntrada().format(DATA_HORA) + ".");
    }

    private void registrarSaida() {
        System.out.println("\n--- SAIDA ---");
        RegistroVisita registro = controleService.registrarSaida(ler("CPF do visitante"));
        System.out.println("Saida registrada as " + registro.getSaida().format(DATA_HORA)
                + ". Permanencia: " + formatar(registro.permanencia(registro.getSaida())) + ".");
    }

    private void mostrarEmAndamento() {
        System.out.println("\n--- NO CONDOMINIO AGORA ---");
        mostrarRegistros(controleService.listarEmAndamento());
    }

    private void historicoPeriodo() {
        LocalDate de = lerData("Data inicial (AAAA-MM-DD, vazio = hoje)");
        LocalDate ate = lerData("Data final (AAAA-MM-DD, vazio = hoje)");
        mostrarRegistros(controleService.historicoPorPeriodo(de, ate));
    }

    private void historicoVisitante() {
        mostrarRegistros(controleService.historicoPorVisitante(ler("CPF do visitante")));
    }

    private void historicoMorador() {
        mostrarRegistros(controleService.historicoPorMorador(ler("CPF do morador")));
    }

    private void resumo() {
        LocalDate de = lerData("Data inicial (AAAA-MM-DD, vazio = hoje)");
        LocalDate ate = lerData("Data final (AAAA-MM-DD, vazio = hoje)");
        ControleVisitaService.Resumo r = controleService.resumo(de, ate);

        System.out.println("\n--- RESUMO " + de + " a " + ate + " ---");
        System.out.println("Total de visitas: " + r.total());
        System.out.println("Concluidas: " + r.concluidas());
        System.out.println("Em andamento: " + r.emAndamento());
        System.out.println("Permanencia media: " + formatar(Duration.ofMinutes(r.tempoMedioMinutos())));
    }

    // ---------- apoio ----------

    private void mostrarVisitante(Visitante v) {
        System.out.println("ID: " + v.getId() + " | Nome: " + v.getNome() + " | CPF: " + v.getCpf());
    }

    private void mostrarRegistros(List<RegistroVisita> registros) {
        if (registros.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        for (RegistroVisita r : registros) {
            Visitante v = visitanteRepository.buscarPorId(r.getVisitanteId());
            Morador m = moradorRepository.buscarPorId(r.getMoradorId());

            System.out.println("\n-----------------------------");
            System.out.println("ID: " + r.getId());
            System.out.println("Visitante: " + (v != null ? v.getNome() : "ID " + r.getVisitanteId()));
            System.out.println("Visitou: " + (m != null ? m.getNome() : "ID " + r.getMoradorId()));
            System.out.println("Entrada: " + r.getEntrada().format(DATA_HORA));
            System.out.println("Saida: " + (r.getSaida() != null ? r.getSaida().format(DATA_HORA) : "ainda no condominio"));
            System.out.println("Permanencia: " + formatar(r.permanencia(agora)));
            System.out.println("Status: " + r.getStatus());
            System.out.println("-----------------------------");
        }
    }

    private String formatar(Duration d) {
        return d.toHours() > 0
                ? d.toHours() + "h" + String.format("%02d", d.toMinutesPart()) + "min"
                : d.toMinutes() + "min";
    }

    private String ler(String campo) {
        System.out.print(campo + ": ");
        return scanner.nextLine().trim();
    }

    // Vazio = hoje.
    private LocalDate lerData(String campo) {
        String texto = ler(campo);
        return texto.isEmpty() ? LocalDate.now() : LocalDate.parse(texto);
    }

    // Permite executar o menu separadamente
    // enquanto o Main do grupo nao esta pronto.
    public static void main(String[] args) {
        new MenuVisitante().exibir();
    }
}
