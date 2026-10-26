package model;

import java.math.BigDecimal;

public class SalaoFestas extends AreaComum{

    public SalaoFestas(Long id) {
        this.id = id;
        this.nome = "Salão de Festas";
    }

    @Override
    public BigDecimal getTaxa() {
        return BigDecimal.valueOf(150);
    }

    @Override
    public int getMaxHoras() {
        return 6;
    }
}
