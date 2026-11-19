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
class ExploradorTest {

    private static final String HABILIDAD = "Marca del Cazador (+1d6)";

    @Test
    @DisplayName("Constructor: nombre de clase, nivel y terreno favorecido (recortado)")
    void constructor() throws ReglaDndException {
        Explorador e = new Explorador(4, "  Bosque  ");
        assertAll(
                () -> assertEquals("Explorador", e.getNombreClase()),
                () -> assertEquals(4, e.getNivel()),
                () -> assertEquals("Bosque", e.getTerrenoFavorecido()));
    }

    @Test
    @DisplayName("Terreno favorecido nulo, vacío o en blanco lanza excepción")
    void terrenoInvalido() {
        String[] invalidos = {null, "", "   "};
        for (String terreno : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Explorador(1, terreno));
            assertEquals("Debe indicar el terreno favorecido del Explorador.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Prerrequisito: Destreza >= 13 y Sabiduría >= 13")
    void prerrequisito() throws ReglaDndException {
        Explorador e = new Explorador(1, "Bosque");
        assertAll(
                () -> assertTrue(e.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 13, 10, 10, 13, 10))),
                () -> assertFalse(e.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 13, 10, 10, 12, 10)),
                        "Falta Sabiduría"),
                () -> assertFalse(e.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 12, 10, 10, 13, 10)),
                        "Falta Destreza"));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Explorador e = new Explorador(1, "Montaña");
        assertEquals("Explorador requiere Destreza 13 y Sabiduría 13.", e.getMensajePrerrequisito());
        assertEquals("terrenoFavorecido: Montaña", e.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: la frase menciona al atacante y su terreno")
    void frase() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Aragorn", 35, 15, 10, 16, 10, 10, 14, 10);
        String log = new Explorador(3, "Bosque").ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(10));
        assertTrue(log.startsWith("El Explorador Aragorn (experto en Bosque) aplica Marca del Cazador"), log);
    }

    @Test
    @DisplayName("Acción: Marca del Cazador usa Destreza y 1d8 (impacta con CA baja)")
    void marcaContraCaBaja() throws ReglaDndException {
        // des 16 → +3
        HeroeGremio atacante = AyudaPruebas.heroe("Aragorn", 35, 15, 10, 16, 10, 10, 14, 10);
        Explorador e = new Explorador(5, "Bosque");
        atacante.agregarClase(e);
        AyudaPruebas.ejecutarYVerificar(e, atacante, AyudaPruebas.objetivo(-100), 300, 3, 1, 8, HABILIDAD);
    }

    @Test
    @DisplayName("Acción: Marca del Cazador falla contra CA muy alta")
    void marcaContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Aragorn", 35, 15, 10, 16, 10, 10, 14, 10);
        Explorador e = new Explorador(5, "Bosque");
        atacante.agregarClase(e);
        AyudaPruebas.ejecutarYVerificar(e, atacante, AyudaPruebas.objetivo(100), 300, 3, 1, 8, HABILIDAD);
    }
}
