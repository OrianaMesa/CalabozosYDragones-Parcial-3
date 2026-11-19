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
class MonjeTest {

    @Test
    @DisplayName("Constructor: nombre de clase, nivel y puntos de Ki")
    void constructor() throws ReglaDndException {
        Monje m = new Monje(4, 5);
        assertAll(
                () -> assertEquals("Monje", m.getNombreClase()),
                () -> assertEquals(4, m.getNivel()),
                () -> assertEquals(5, m.getPuntosKi()));
    }

    @Test
    @DisplayName("0 puntos de Ki es válido; negativos lanzan excepción")
    void kiValidacion() throws ReglaDndException {
        assertEquals(0, new Monje(1, 0).getPuntosKi());
        ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Monje(1, -1));
        assertEquals("Los puntos de Ki no pueden ser negativos.", ex.getMessage());
    }

    @Test
    @DisplayName("Prerrequisito: Destreza >= 13 y Sabiduría >= 13")
    void prerrequisito() throws ReglaDndException {
        Monje m = new Monje(1, 1);
        assertAll(
                () -> assertTrue(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 13, 10, 10, 13, 10))),
                () -> assertFalse(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 13, 10, 10, 12, 10)),
                        "Falta Sabiduría"),
                () -> assertFalse(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 12, 10, 10, 13, 10)),
                        "Falta Destreza"));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Monje m = new Monje(1, 7);
        assertEquals("Monje requiere Destreza 13 y Sabiduría 13.", m.getMensajePrerrequisito());
        assertEquals("puntosKi: 7", m.getAtributoEspecial());
    }

    @Test
    @DisplayName("Cada acción gasta 1 punto de Ki y lo informa en la frase")
    void accionGastaKi() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Kai", 30, 14, 10, 16, 10, 10, 14, 10);
        Monje m = new Monje(3, 2);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String primero = m.ejecutarAccionTactica(atacante, objetivo);
        assertTrue(primero.startsWith("El Monje Kai gasta puntos de Ki (restan 1)"), primero);
        assertEquals(1, m.getPuntosKi());

        String segundo = m.ejecutarAccionTactica(atacante, objetivo);
        assertTrue(segundo.contains("(restan 0)"));
        assertEquals(0, m.getPuntosKi());
    }

    @Test
    @DisplayName("Sin Ki el Monje sigue atacando y el Ki no baja de 0")
    void sinKiSigueAtacando() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Kai", 30, 14, 10, 16, 10, 10, 14, 10);
        Monje m = new Monje(3, 0);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String log = m.ejecutarAccionTactica(atacante, objetivo);

        assertEquals(0, m.getPuntosKi());
        assertTrue(log.contains("(restan 0)"));
        assertTrue(log.contains("Tirada d20"), "Debe resolver un ataque aunque no quede Ki");
    }

    @Test
    @DisplayName("Acción: Ráfaga de Golpes usa Destreza y 2d6 (impacta con CA baja)")
    void rafagaContraCaBaja() throws ReglaDndException {
        // des 16 → +3
        HeroeGremio atacante = AyudaPruebas.heroe("Kai", 30, 14, 10, 16, 10, 10, 14, 10);
        Monje m = new Monje(5, 100000);
        atacante.agregarClase(m);
        AyudaPruebas.ejecutarYVerificar(m, atacante, AyudaPruebas.objetivo(-100), 300, 3, 2, 6, "Ráfaga de Golpes");
    }

    @Test
    @DisplayName("Acción: Ráfaga de Golpes falla contra CA muy alta")
    void rafagaContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Kai", 30, 14, 10, 16, 10, 10, 14, 10);
        Monje m = new Monje(5, 100000);
        atacante.agregarClase(m);
        AyudaPruebas.ejecutarYVerificar(m, atacante, AyudaPruebas.objetivo(100), 300, 3, 2, 6, "Ráfaga de Golpes");
    }
}
