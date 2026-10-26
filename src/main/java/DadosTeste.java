import model.Apartamento;
import model.Morador;
import repository.ApartamentoRepository;
import repository.AreaComumRepository;
import repository.MoradorRepository;

public class DadosTeste {

    public static void popular(ApartamentoRepository apartamentoRepository, AreaComumRepository areaComumRepository, MoradorRepository moradorRepository) {
        Apartamento ap1 = new Apartamento("Torre 1", "18A");
        Apartamento ap2 = new Apartamento("Torre 1", "16B");
        apartamentoRepository.salvar(ap1);
        apartamentoRepository.salvar(ap2);

        Morador m1 = new Morador("João", "23923721936", ap1.getId(), 0, 1);
        Morador m2 = new Morador("Maria", "126789362174", ap2.getId(), 0, 1);
        moradorRepository.salvar(m1);
        moradorRepository.salvar(m2);
    }
}
