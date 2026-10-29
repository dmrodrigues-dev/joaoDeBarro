package repository;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import model.Identificavel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CsvRepository<T extends Identificavel> {

    private final Path path;
    private final Class<T> clazz;
    protected final List<T> dados = new ArrayList<T>();

    public CsvRepository(Path path, Class<T> clazz) {
        this.path = path;
        this.clazz = clazz;
        carregar();
    }

    private void carregar() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return;
            }
            if (Files.size(path) == 0) return;

            try (Reader reader = new FileReader(path.toFile())) {
                dados.addAll(new CsvToBeanBuilder<T>(reader)
                        .withType(clazz)
                        .build()
                        .parse());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gravar() {
        try (Writer writer  = new FileWriter(path.toString())) {
            new StatefulBeanToCsvBuilder<T>(writer)
                    .build()
                    .write(dados);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public List<T> listarTudo() {
        return List.copyOf(dados);
    }

    public T buscarPorId(Long id) {
        return dados.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst().orElse(null);
    }

    private Long proximoId() {
        return dados.stream()
                .mapToLong(e -> e.getId())
                .max()
                .orElse(0) +1;
    }

    public void salvar(T entidade) {

        if (entidade.getId() == null) {
            entidade.setId(proximoId());
        }
        dados.add(entidade);
        gravar();
    }

    public void remover(Long id) {
        dados.removeIf(e -> e.getId().equals(id));
        gravar();
    }
}
