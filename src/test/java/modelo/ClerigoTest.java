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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author orian
 */
class ClerigoTest {

    private static final Pattern CURACION = Pattern.compile("\\(\\+(\\d+) PG\\)");

    @Test
    @DisplayName("Constructor: nombre de clase (con tilde), nivel y dios (recortado)")
    void constructor() throws ReglaDndException {
        Clerigo c = new Clerigo(3, "  Pelor  ");
        assertAll(
                () -> assertEquals("Clérigo", c.getNombreClase()),
                () -> assertEquals(3, c.getNivel()),
                () -> assertEquals("Pelor", c.getDiosVenerado()));
    }

    @Test
    @DisplayName("Dios nulo, vacío o en blanco lanza excepción")
    void diosInvalido() {
        String[] invalidos = {null, "", "   "};
        for (String dios : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Clerigo(1, dios));
            assertEquals("Debe indicar el dios venerado del Clérigo.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Prerrequisito: Sabiduría >= 13 (límite 12/13)")
    void prerrequisito() throws ReglaDndException {
        Clerigo c = new Clerigo(1, "Pelor");
        assertFalse(c.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(18, 18, 18, 18, 12, 18)));
        assertTrue(c.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(8, 8, 8, 8, 13, 8)));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Clerigo c = new Clerigo(1, "Tyr");
        assertEquals("Clérigo requiere Sabiduría 13.", c.getMensajePrerrequisito());
        assertEquals("diosVenerado: Tyr", c.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: cura al atacante entre 1d8+mod PG y lo informa en la frase")
    void accionCuraAlAtacante() throws ReglaDndException {
        // sab 14 → +2 → curación en [3, 10]
        for (int i = 0; i < 300; i++) {
            HeroeGremio atacante = AyudaPruebas.heroe("Elara", 100, 12, 10, 10, 10, 10, 14, 10);
            atacante.recibirDanio(60);
            Clerigo c = new Clerigo(3, "Pelor");

            String log = c.ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(100));

            Matcher m = CURACION.matcher(log);
            assertTrue(m.find(), "La frase debe indicar la curación: " + log);
            int curacion = Integer.parseInt(m.group(1));
            assertTrue(curacion >= 3 && curacion <= 10, "Curación fuera de rango [3,10]: " + curacion);
            assertEquals(40 + curacion, atacante.getPuntosVidaActual(),
                    "Los PG del atacante deben subir exactamente lo informado");
            assertTrue(log.startsWith("El Clérigo Elara invoca a Pelor y usa Canalizar Divinidad"), log);
        }
    }

    @Test
    @DisplayName("Acción: la curación no supera los PG máximos del atacante")
    void curacionNoSuperaMaximo() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Elara", 100, 12, 10, 10, 10, 10, 14, 10);
        Clerigo c = new Clerigo(3, "Pelor");
        for (int i = 0; i < 50; i++) {
            c.ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(100));
            assertEquals(100, atacante.getPuntosVidaActual());
        }
    }

    @Test
    @DisplayName("Acción: Llama Sagrada usa Sabiduría y 1d8 (impacta con CA baja)")
    void llamaContraCaBaja() throws ReglaDndException {
        // sab 14 → +2
        HeroeGremio atacante = AyudaPruebas.heroe("Elara", 100, 12, 10, 10, 10, 10, 14, 10);
        Clerigo c = new Clerigo(5, "Pelor");
        atacante.agregarClase(c);
        AyudaPruebas.ejecutarYVerificar(c, atacante, AyudaPruebas.objetivo(-100), 300, 2, 1, 8, "Llama Sagrada");
    }

    @Test
    @DisplayName("Acción: Llama Sagrada falla contra CA muy alta")
    void llamaContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Elara", 100, 12, 10, 10, 10, 10, 14, 10);
        Clerigo c = new Clerigo(5, "Pelor");
        atacante.agregarClase(c);
        AyudaPruebas.ejecutarYVerificar(c, atacante, AyudaPruebas.objetivo(100), 300, 2, 1, 8, "Llama Sagrada");
    }
}

