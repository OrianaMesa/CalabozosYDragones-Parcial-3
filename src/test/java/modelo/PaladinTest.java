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
class PaladinTest {

    @Test
    @DisplayName("Constructor: nombre de clase (con tilde), nivel y puntos de fuerza")
    void constructor() throws ReglaDndException {
        Paladin p = new Paladin(6, 3);
        assertAll(
                () -> assertEquals("Paladín", p.getNombreClase()),
                () -> assertEquals(6, p.getNivel()),
                () -> assertEquals(3, p.getPuntosFuerza()));
    }

    @Test
    @DisplayName("0 puntos de fuerza es válido; negativos lanzan excepción")
    void puntosFuerzaValidacion() throws ReglaDndException {
        assertEquals(0, new Paladin(1, 0).getPuntosFuerza());
        ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Paladin(1, -1));
        assertEquals("Los puntos de fuerza/imposición no pueden ser negativos.", ex.getMessage());
    }

    @Test
    @DisplayName("Prerrequisito: Fuerza >= 13 y Carisma >= 13")
    void prerrequisito() throws ReglaDndException {
        Paladin p = new Paladin(1, 1);
        assertAll(
                () -> assertTrue(p.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(13, 10, 10, 10, 10, 13))),
                () -> assertFalse(p.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(13, 10, 10, 10, 10, 12)),
                        "Falta Carisma"),
                () -> assertFalse(p.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(12, 10, 10, 10, 10, 13)),
                        "Falta Fuerza"));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Paladin p = new Paladin(1, 4);
        assertEquals("Paladín requiere Fuerza 13 y Carisma 13.", p.getMensajePrerrequisito());
        assertEquals("puntosFuerza: 4", p.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: la frase incluye el nombre del atacante y su Poder")
    void frase() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Arthas", 40, 18, 16, 10, 10, 10, 10, 14);
        String log = new Paladin(3, 7).ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(10));
        assertTrue(log.startsWith("El Paladín Arthas (Poder: 7) consume magia"), log);
    }

    @Test
    @DisplayName("Acción: Castigo Divino usa Fuerza y 2d8 (impacta con CA baja)")
    void castigoContraCaBaja() throws ReglaDndException {
        // fue 16 → +3
        HeroeGremio atacante = AyudaPruebas.heroe("Arthas", 40, 18, 16, 10, 10, 10, 10, 14);
        Paladin p = new Paladin(5, 2);
        atacante.agregarClase(p);
        AyudaPruebas.ejecutarYVerificar(p, atacante, AyudaPruebas.objetivo(-100), 300, 3, 2, 8, "Castigo Divino");
    }

    @Test
    @DisplayName("Acción: Castigo Divino falla contra CA muy alta")
    void castigoContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Arthas", 40, 18, 16, 10, 10, 10, 10, 14);
        Paladin p = new Paladin(5, 2);
        atacante.agregarClase(p);
        AyudaPruebas.ejecutarYVerificar(p, atacante, AyudaPruebas.objetivo(100), 300, 3, 2, 8, "Castigo Divino");
    }
}
