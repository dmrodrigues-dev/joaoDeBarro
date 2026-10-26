package repository;

import model.*;

import java.util.List;

public class AreaComumRepository {
    private final List<AreaComum> areaComums = List.of(
            new Churrasqueira(1L),
            new Academia(2L),
            new Piscina(3L),
            new SalaoFestas(4L)
    );

    public List<AreaComum> listarTodos() {
        return areaComums;
    }

    public AreaComum buscarPorId(Long id) {
        return areaComums.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
