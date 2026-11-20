package service;

import model.Autorizacao;
import model.Morador;
import model.RegistroVisita;
import model.Visitante;
import repository.AutorizacaoRepository;
import repository.MoradorRepository;
import repository.RegistroVisitaRepository;
import repository.VisitanteRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/*
 * Controle de entrada e saída de visitantes.
 *
 * Regra principal do enunciado: o visitante só entra se um morador
 * o autorizou para aquela data (Autorizacao, criada pelo AutorizacaoService).
 *
 * Seguindo o ReservaService, os erros são lançados como exceção
 * (IllegalArgumentException / IllegalStateException) e quem chama
 * (o menu) mostra a mensagem. Nada é impresso aqui.
 */
public class ControleVisitaService {

    private final MoradorRepository moradorRepository;
    private final VisitanteRepository visitanteRepository;
    private final AutorizacaoRepository autorizacaoRepository;
    private final RegistroVisitaRepository registroRepository;
    private final Clock relogio;

    public ControleVisitaService(MoradorRepository moradorRepository,
                                 VisitanteRepository visitanteRepository,
                                 AutorizacaoRepository autorizacaoRepository,
                                 RegistroVisitaRepository registroRepository) {
        this(moradorRepository, visitanteRepository, autorizacaoRepository,
                registroRepository, Clock.systemDefaultZone());
    }

    // Usado nos testes, para controlar o "agora".
    public ControleVisitaService(MoradorRepository moradorRepository,
                                 VisitanteRepository visitanteRepository,
                                 AutorizacaoRepository autorizacaoRepository,
                                 RegistroVisitaRepository registroRepository,
                                 Clock relogio) {
        this.moradorRepository = moradorRepository;
        this.visitanteRepository = visitanteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
        this.registroRepository = registroRepository;
        this.relogio = relogio;
    }

    // Moradores que autorizaram esse visitante para hoje.
    public List<Morador> moradoresAutorizadosHoje(String cpfVisitante) {
        Visitante visitante = exigirVisitante(cpfVisitante);
        List<Autorizacao> autorizacoes =
                autorizacaoRepository.buscarVisitanteData(visitante.getId(), hoje());

        return autorizacoes.stream()
                .map(a -> moradorRepository.buscarPorId(a.getMoradorId()))
                .filter(Objects::nonNull)
                .toList();
    }

    /*
     * Entrada quando só um morador autorizou o visitante hoje.
     * Se mais de um autorizou, é preciso dizer qual vai receber
     * (use a versão com o CPF do morador).
     */
    public RegistroVisita registrarEntrada(String cpfVisitante) {
        List<Morador> autorizadores = moradoresAutorizadosHoje(cpfVisitante);

        if (autorizadores.isEmpty()) {
            throw new IllegalStateException(
                    "Entrada negada: nenhum morador autorizou este visitante para hoje.");
        }
        if (autorizadores.size() > 1) {
            throw new IllegalArgumentException(
                    "Este visitante foi autorizado por mais de um morador hoje: informe o CPF do morador que vai recebê-lo.");
        }
        return registrarEntrada(cpfVisitante, autorizadores.get(0).getCpf());
    }

    public RegistroVisita registrarEntrada(String cpfVisitante, String cpfMorador) {
        Visitante visitante = exigirVisitante(cpfVisitante);
        Morador morador = exigirMorador(cpfMorador);

        if (autorizacaoRepository.buscarMoradorVisitanteData(
                morador.getId(), visitante.getId(), hoje()) == null) {
            throw new IllegalStateException(
                    "Entrada negada: este morador não autorizou o visitante para hoje.");
        }

        if (registroRepository.buscarAtivoPorVisitante(visitante.getId()) != null) {
            throw new IllegalStateException(
                    "Este visitante já está no condomínio. Registre a saída antes de uma nova entrada.");
        }

        RegistroVisita registro = new RegistroVisita(visitante.getId(), morador.getId(), agora());
        registroRepository.salvar(registro);
        return registro;
    }

    public RegistroVisita registrarSaida(String cpfVisitante) {
        Visitante visitante = exigirVisitante(cpfVisitante);

        RegistroVisita registro = registroRepository.buscarAtivoPorVisitante(visitante.getId());
        if (registro == null) {
            throw new IllegalStateException("Este visitante não está no condomínio.");
        }

        registro.registrarSaida(agora());
        registroRepository.atualizar(registro);
        return registro;
    }

    // Quem está dentro do condomínio agora.
    public List<RegistroVisita> listarEmAndamento() {
        return registroRepository.buscarAtivos();
    }

    public List<RegistroVisita> listarTodos() {
        return registroRepository.listarTudo();
    }

    public List<RegistroVisita> historicoPorPeriodo(LocalDate de, LocalDate ate) {
        if (de == null || ate == null) {
            throw new IllegalArgumentException("Informe a data inicial e a final.");
        }
        if (de.isAfter(ate)) {
            throw new IllegalArgumentException("A data inicial não pode ser depois da final.");
        }
        return registroRepository.buscarPorPeriodo(de, ate);
    }

    public List<RegistroVisita> historicoPorVisitante(String cpfVisitante) {
        return registroRepository.buscarPorVisitante(exigirVisitante(cpfVisitante).getId());
    }

    public List<RegistroVisita> historicoPorMorador(String cpfMorador) {
        return registroRepository.buscarPorMorador(exigirMorador(cpfMorador).getId());
    }

    // Números de um período, para o relatório da administração.
    public Resumo resumo(LocalDate de, LocalDate ate) {
        List<RegistroVisita> lista = historicoPorPeriodo(de, ate);
        LocalDateTime agora = agora();

        int emAndamento = (int) lista.stream().filter(RegistroVisita::estaEmAndamento).count();
        List<RegistroVisita> concluidas = lista.stream()
                .filter(r -> !r.estaEmAndamento())
                .toList();

        long tempoMedio = concluidas.isEmpty() ? 0 : Math.round(concluidas.stream()
                .mapToLong(r -> r.permanencia(agora).toMinutes())
                .average()
                .orElse(0));

        return new Resumo(lista.size(), emAndamento, concluidas.size(), tempoMedio);
    }

    public record Resumo(int total, int emAndamento, int concluidas, long tempoMedioMinutos) {}

    // ---------- apoio ----------

    private Visitante exigirVisitante(String cpf) {
        Visitante visitante = visitanteRepository.buscarCpf(exigirTexto(cpf, "CPF do visitante"));
        if (visitante == null) {
            throw new IllegalArgumentException("Visitante não encontrado.");
        }
        return visitante;
    }

    private Morador exigirMorador(String cpf) {
        Morador morador = moradorRepository.buscarCpf(exigirTexto(cpf, "CPF do morador"));
        if (morador == null) {
            throw new IllegalArgumentException("Morador não encontrado.");
        }
        return morador;
    }

    private String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    private LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    // Sem segundos: o CSV guarda só até os minutos.
    private LocalDateTime agora() {
        return LocalDateTime.now(relogio).truncatedTo(ChronoUnit.MINUTES);
    }
}
