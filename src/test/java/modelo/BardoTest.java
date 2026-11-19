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
class BardoTest {

    @Test
    @DisplayName("Constructor: nombre de clase, nivel e instrumento (recortado)")
    void constructor() throws ReglaDndException {
        Bardo b = new Bardo(3, "  Laúd  ");
        assertAll(
                () -> assertEquals("Bardo", b.getNombreClase()),
                () -> assertEquals(3, b.getNivel()),
                () -> assertEquals("Laúd", b.getInstrumentoMusical()));
    }

    @Test
    @DisplayName("Instrumento nulo, vacío o en blanco lanza excepción")
    void instrumentoInvalido() {
        String[] invalidos = {null, "", "   "};
        for (String instrumento : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Bardo(1, instrumento));
            assertEquals("Debe indicar el instrumento musical del Bardo.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Prerrequisito: Carisma >= 13 (límite 12/13)")
    void prerrequisito() throws ReglaDndException {
        Bardo b = new Bardo(1, "Flauta");
        assertFalse(b.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(18, 18, 18, 18, 18, 12)));
        assertTrue(b.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(8, 8, 8, 8, 8, 13)));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Bardo b = new Bardo(1, "Flauta");
        assertEquals("Bardo requiere Carisma 13.", b.getMensajePrerrequisito());
        assertEquals("instrumentoMusical: Flauta", b.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: la frase menciona al atacante y su instrumento")
    void frase() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Jaskier", 25, 12, 10, 10, 10, 10, 10, 16);
        String log = new Bardo(3, "Laúd").ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(10));
        assertTrue(log.startsWith("El Bardo Jaskier toca su Laúd para otorgar Inspiración Bárdica a un aliado."), log);
    }

    @Test
    @DisplayName("Acción: Burla Dañina usa Carisma y 1d6 (impacta con CA baja)")
    void burlaContraCaBaja() throws ReglaDndException {
        // car 16 → +3
        HeroeGremio atacante = AyudaPruebas.heroe("Jaskier", 25, 12, 10, 10, 10, 10, 10, 16);
        Bardo b = new Bardo(3, "Laúd");
        atacante.agregarClase(b);
        AyudaPruebas.ejecutarYVerificar(b, atacante, AyudaPruebas.objetivo(-100), 300, 3, 1, 6, "Burla Dañina");
    }

    @Test
    @DisplayName("Acción: Burla Dañina falla contra CA muy alta")
    void burlaContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Jaskier", 25, 12, 10, 10, 10, 10, 10, 16);
        Bardo b = new Bardo(3, "Laúd");
        atacante.agregarClase(b);
        AyudaPruebas.ejecutarYVerificar(b, atacante, AyudaPruebas.objetivo(100), 300, 3, 1, 6, "Burla Dañina");
    }
}
