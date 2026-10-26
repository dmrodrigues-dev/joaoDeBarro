import model.AreaComum;
import model.Morador;
import repository.*;
import service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        ApartamentoRepository apartamentoRepository = new ApartamentoRepository();
        MoradorRepository moradorRepository = new MoradorRepository();
        AreaComumRepository areaComumRepository = new AreaComumRepository();
        CobrancaRepository cobrancaRepository = new CobrancaRepository();
        ReservaRepository reservaRepository = new ReservaRepository();

        DadosTeste.popular(apartamentoRepository, areaComumRepository, moradorRepository);

        Morador maria = moradorRepository.buscarCpf("126789362174");
        AreaComum churras = areaComumRepository.buscarPorId(2L);

        ReservaService reservaService = new ReservaService(reservaRepository, cobrancaRepository);

        LocalDate hoje = LocalDate.now();

        reservaService.reservarAreaComum(maria, churras, 2, LocalTime.of(18, 00), hoje);

        reservaService.reservarAreaComum(maria, churras, 2, LocalTime.of(17, 00), hoje);
    }
}