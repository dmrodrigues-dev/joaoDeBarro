package model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AreaComumTest {
    @Test
    public void churrasqueiraDeveCobrarOitentaReaisELimitarQuatroHoras() {
        AreaComum area = new Churrasqueira(1L);
        assertEquals(new BigDecimal("80"), area.getTaxa());
        assertEquals(4, area.getMaxHoras());
    }

    @Test
    public void salaoDeFestasDeveCobrarCentoECinquentaReaisELimitarSeisHoras() {
        AreaComum area = new SalaoFestas(2L);
        assertEquals(new BigDecimal("150"), area.getTaxa());
        assertEquals(6, area.getMaxHoras());
    }

    @Test
    public void piscinaEAcademiaDevemSerGratuitasELimitarDuasHoras() {
        AreaComum piscina = new Piscina(3L);
        AreaComum academia = new Academia(4L);

        assertEquals(BigDecimal.ZERO, piscina.getTaxa());
        assertEquals(2, piscina.getMaxHoras());
        assertEquals(BigDecimal.ZERO, academia.getTaxa());
        assertEquals(2, academia.getMaxHoras());
    }
}
