/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
/**
 *
 * @author orian
 */
class ClasePersonajeTest {

    @Test
    @DisplayName("Niveles 1 y 20 son válidos (límites del rango)")
    void nivelesLimiteValidos() throws ReglaDndException {
        assertEquals(1, new Guerrero(1, false).getNivel());
        assertEquals(20, new Guerrero(20, false).getNivel());
    }

    @Test
    @DisplayName("Niveles fuera de 1..20 lanzan ReglaDndException con el mensaje correcto")
    void nivelesInvalidosLanzanExcepcion() {
        int[] invalidos = {0, -1, 21, 100};
        for (int nivel : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class,
                    () -> new Guerrero(nivel, true), "Nivel " + nivel + " debería ser inválido");
            assertEquals("El nivel de la clase Guerrero debe estar entre 1 y 20.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Toda clase de personaje implementa IAccionCombate")
    void implementaInterfazDeCombate() throws ReglaDndException {
        ClasePersonaje clase = new Guerrero(3, true);
        assertTrue(clase instanceof IAccionCombate);
    }

    @Test
    @DisplayName("Con CA muy baja siempre impacta (salvo pifia) y el daño está dentro del rango")
    void ataqueContraCaBajaImpacta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        Guerrero guerrero = new Guerrero(5, true);
        atacante.agregarClase(guerrero);
        HeroeGremio objetivo = AyudaPruebas.objetivo(-100);

        AyudaPruebas.ejecutarYVerificar(guerrero, atacante, objetivo, 300,
                atacante.getModFuerza(), 2, 8, "Oleada de Acción");
    }

    @Test
    @DisplayName("Con CA muy alta solo impacta con crítico y el resto son fallos sin daño")
    void ataqueContraCaAltaFalla() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        Guerrero guerrero = new Guerrero(5, true);
        atacante.agregarClase(guerrero);
        HeroeGremio objetivo = AyudaPruebas.objetivo(100);

        AyudaPruebas.ejecutarYVerificar(guerrero, atacante, objetivo, 300,
                atacante.getModFuerza(), 2, 8, "Oleada de Acción");
    }

    @Test
    @DisplayName("Se producen tanto críticos (20 natural) como pifias (1 natural) y ambos se resuelven bien")
    void seAlcanzanCriticoYPifia() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        Guerrero guerrero = new Guerrero(5, true);
        atacante.agregarClase(guerrero);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        boolean vistoCritico = false;
        boolean vistaPifia = false;
        for (int i = 0; i < 5000 && !(vistoCritico && vistaPifia); i++) {
            objetivo.restaurarVida();
            int pvAntes = objetivo.getPuntosVidaActual();
            String log = guerrero.ejecutarAccionTactica(atacante, objetivo);
            String r = AyudaPruebas.verificarAtaque(log, atacante, objetivo, pvAntes,
                    atacante.getModFuerza(), 2, 8, "Oleada de Acción");
            vistoCritico |= r.equals("CRITICO");
            vistaPifia |= r.equals("PIFIA");
        }
        final boolean critico = vistoCritico;
        final boolean pifia = vistaPifia;
        assertAll(
                () -> assertTrue(critico, "Debió aparecer al menos un crítico"),
                () -> assertTrue(pifia, "Debió aparecer al menos una pifia"));
    }

    @Test
    @DisplayName("Un impacto sobre un objetivo con 1 PG lo deja en 0 PG y fuera de combate")
    void impactoNoBajaDeCeroPg() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        Guerrero guerrero = new Guerrero(5, true);
        atacante.agregarClase(guerrero);

        boolean huboImpacto = false;
        for (int i = 0; i < 100; i++) {
            HeroeGremio objetivo = AyudaPruebas.heroe("Dummy", 1, -100, 10, 10, 10, 10, 10, 10);
            String log = guerrero.ejecutarAccionTactica(atacante, objetivo);
            if (log.contains("IMPACTO") || log.contains("CRÍTICO")) {
                huboImpacto = true;
                assertEquals(0, objetivo.getPuntosVidaActual());
                assertFalse(objetivo.estaVivo());
                assertTrue(log.contains("PG restantes: 0)"));
            } else {
                assertEquals(1, objetivo.getPuntosVidaActual(), "Sin impacto no debe perder PG");
            }
        }
        assertTrue(huboImpacto, "Debió haber al menos un impacto");
    }
}
