
package view;

import model.AreaComum;
import model.Morador;
import model.Reserva;
import repository.AreaComumRepository;
import repository.CobrancaRepository;
import repository.MoradorRepository;
import repository.ReservaRepository;
import service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class MenuReserva {

    private final Scanner scanner;
    private final ReservaService service;
    private final MoradorRepository moradorRepository;
    private final AreaComumRepository areaRepository;

    public MenuReserva() {
        this.scanner = new Scanner(System.in);

        ReservaRepository reservaRepository = new ReservaRepository();
        CobrancaRepository cobrancaRepository = new CobrancaRepository();

        this.moradorRepository = new MoradorRepository();
        this.areaRepository = new AreaComumRepository();

        this.service = new ReservaService(
                reservaRepository,
                cobrancaRepository
        );
    }

    public void exibir() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n========== RESERVAS ==========");
            System.out.println("1 - Listar areas comuns");
            System.out.println("2 - Cadastrar reserva");
            System.out.println("3 - Listar reservas");
            System.out.println("4 - Buscar reserva por ID");
            System.out.println("5 - Consultar reservas por morador");
            System.out.println("6 - Consultar reservas por data");
            System.out.println("7 - Cancelar reserva");
            System.out.println("8 - Concluir reserva");
            System.out.println("0 - Voltar / Sair");
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(
                        scanner.nextLine().trim()
                );

                switch (opcao) {
                    case 1 -> listarAreas();
                    case 2 -> cadastrar();
                    case 3 -> listarReservas();
                    case 4 -> buscar();
                    case 5 -> consultarMorador();
                    case 6 -> consultarData();
                    case 7 -> cancelar();
                    case 8 -> concluir();
                    case 0 -> System.out.println(
                            "Saindo do menu de reservas."
                    );
                    default -> System.out.println(
                            "Opcao invalida."
                    );
                }

            } catch (IllegalArgumentException |
                     IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void listarAreas() {
        System.out.println("\n--- AREAS COMUNS ---");

        for (AreaComum area : areaRepository.listarTodos()) {
            System.out.println(
                    "ID: " + area.getId()
                            + " | Area: " + area.getNome()
                            + " | Limite: " + area.getMaxHoras() + " horas"
                            + " | Taxa: R$ " + area.getTaxa()
            );
        }
    }

    private void cadastrar() {
        System.out.println("\n--- NOVA RESERVA ---");

        System.out.print("ID do morador: ");
        Long moradorId = Long.parseLong(
                scanner.nextLine().trim()
        );

        Morador morador = moradorRepository.buscarPorId(moradorId);

        if (morador == null) {
            throw new IllegalArgumentException(
                    "Morador nao encontrado."
            );
        }

        listarAreas();

        System.out.print("ID da area: ");
        Long areaId = Long.parseLong(
                scanner.nextLine().trim()
        );

        AreaComum area = areaRepository.buscarPorId(areaId);

        if (area == null) {
            throw new IllegalArgumentException(
                    "Area nao encontrada."
            );
        }

        System.out.print("Data (AAAA-MM-DD): ");
        LocalDate data = LocalDate.parse(
                scanner.nextLine().trim()
        );

        System.out.print("Horario inicial (HH:mm): ");
        LocalTime inicio = LocalTime.parse(
                scanner.nextLine().trim()
        );

        System.out.print("Quantidade de horas: ");
        int horas = Integer.parseInt(
                scanner.nextLine().trim()
        );

        service.reservarAreaComum(
                morador, area, horas, inicio, data
        );

        System.out.println(
                "Reserva cadastrada com sucesso!"
        );
    }

    private void listarReservas() {
        List<Reserva> reservas = service.listarReservas();

        if (reservas.isEmpty()) {
            System.out.println(
                    "Nenhuma reserva encontrada."
            );
            return;
        }

        for (Reserva reserva : reservas) {
            mostrarReserva(reserva);
        }
    }

    private void buscar() {
        Long id = lerId();
        mostrarReserva(service.buscarPorId(id));
    }

    private void consultarMorador() {
        System.out.print("ID do morador: ");
        Long id = Long.parseLong(
                scanner.nextLine().trim()
        );

        List<Reserva> reservas = service.buscarPorMorador(id);

        if (reservas.isEmpty()) {
            System.out.println(
                    "Nenhuma reserva encontrada."
            );
            return;
        }

        for (Reserva reserva : reservas) {
            mostrarReserva(reserva);
        }
    }

    private void consultarData() {
        System.out.print("Data (AAAA-MM-DD): ");
        LocalDate data = LocalDate.parse(
                scanner.nextLine().trim()
        );

        List<Reserva> reservas = service.buscarPorData(data);

        if (reservas.isEmpty()) {
            System.out.println(
                    "Nenhuma reserva encontrada."
            );
            return;
        }

        for (Reserva reserva : reservas) {
            mostrarReserva(reserva);
        }
    }

    private void cancelar() {
        Long id = lerId();

        service.cancelarReserva(id);

        System.out.println(
                "Reserva cancelada com sucesso!"
        );
    }

    private void concluir() {
        Long id = lerId();

        service.concluirReserva(id);

        System.out.println(
                "Reserva concluida com sucesso!"
        );
    }

    private Long lerId() {
        System.out.print("Informe o ID da reserva: ");

        return Long.parseLong(
                scanner.nextLine().trim()
        );
    }

    private void mostrarReserva(Reserva reserva) {
        System.out.println(
                "\n-----------------------------"
        );
        System.out.println("ID: " + reserva.getId());
        System.out.println("Morador ID: " + reserva.getMoradorId());
        System.out.println("Area ID: " + reserva.getAreaId());
        System.out.println("Data: " + reserva.getData());
        System.out.println("Inicio: " + reserva.getInicio());
        System.out.println(
                "Duracao: " + reserva.getHoras() + " horas"
        );
        System.out.println("Status: " + reserva.getStatus());
        System.out.println(
                "-----------------------------"
        );
    }

    // Permite executar o menu separadamente
    // enquanto o Main do grupo nao esta pronto.
    public static void main(String[] args) {
        MenuReserva menu = new MenuReserva();
        menu.exibir();
    }
}
