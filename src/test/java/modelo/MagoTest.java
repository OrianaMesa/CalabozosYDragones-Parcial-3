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
class MagoTest {

    @Test
    @DisplayName("Constructor: nombre de clase, nivel y espacios de conjuro")
    void constructor() throws ReglaDndException {
        Mago m = new Mago(4, 6);
        assertAll(
                () -> assertEquals("Mago", m.getNombreClase()),
                () -> assertEquals(4, m.getNivel()),
                () -> assertEquals(6, m.getEspaciosConjuro()));
    }

    @Test
    @DisplayName("0 espacios de conjuro es válido; negativos lanzan excepción")
    void espaciosDeConjuroValidacion() throws ReglaDndException {
        assertEquals(0, new Mago(1, 0).getEspaciosConjuro());
        ReglaDndException ex = assertThrows(ReglaDndException.class, () -> new Mago(1, -1));
        assertEquals("Los espacios de conjuro no pueden ser negativos.", ex.getMessage());
    }

    @Test
    @DisplayName("Prerrequisito: Inteligencia >= 13 (límite 12/13)")
    void prerrequisito() throws ReglaDndException {
        Mago m = new Mago(1, 1);
        assertFalse(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 10, 10, 12, 10, 10)));
        assertTrue(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(10, 10, 10, 13, 10, 10)));
        assertFalse(m.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(18, 18, 18, 8, 18, 18)),
                "Solo importa la Inteligencia");
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        Mago m = new Mago(1, 3);
        assertEquals("Mago requiere Inteligencia 13.", m.getMensajePrerrequisito());
        assertEquals("espaciosConjuro: 3", m.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción con espacios: gasta uno y lo informa en la frase")
    void accionGastaEspacio() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Merlín", 20, 10, 10, 10, 10, 16, 10, 10);
        Mago m = new Mago(3, 3);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String log = m.ejecutarAccionTactica(atacante, objetivo);

        assertTrue(log.startsWith("El Mago Merlín gasta un espacio de conjuro (restan 2) para lanzar una poderosa Bola de Fuego."),
                log);
        assertEquals(2, m.getEspaciosConjuro());
        assertEquals("espaciosConjuro: 2", m.getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: Bola de Fuego usa Inteligencia y 3d6 (impacta con CA baja)")
    void bolaDeFuegoContraCaBaja() throws ReglaDndException {
        // int 16 → +3
        HeroeGremio atacante = AyudaPruebas.heroe("Merlín", 20, 10, 10, 10, 10, 16, 10, 10);
        Mago m = new Mago(3, 100000);
        atacante.agregarClase(m);
        AyudaPruebas.ejecutarYVerificar(m, atacante, AyudaPruebas.objetivo(-100), 300, 3, 3, 6, "Bola de Fuego");
    }

    @Test
    @DisplayName("Acción: Bola de Fuego falla contra CA muy alta")
    void bolaDeFuegoContraCaAlta() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Merlín", 20, 10, 10, 10, 10, 16, 10, 10);
        Mago m = new Mago(3, 100000);
        atacante.agregarClase(m);
        AyudaPruebas.ejecutarYVerificar(m, atacante, AyudaPruebas.objetivo(100), 300, 3, 3, 6, "Bola de Fuego");
    }

    @Test
    @DisplayName("Los espacios se agotan: después solo lanza un truco y no ataca")
    void sinEspaciosLanzaTruco() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Merlín", 20, 10, 10, 10, 10, 16, 10, 10);
        Mago m = new Mago(2, 2);
        HeroeGremio objetivo = AyudaPruebas.objetivo(-100);

        String primero = m.ejecutarAccionTactica(atacante, objetivo);
        String segundo = m.ejecutarAccionTactica(atacante, objetivo);
        assertTrue(primero.contains("restan 1"));
        assertTrue(segundo.contains("restan 0"));
        assertEquals(0, m.getEspaciosConjuro());

        objetivo.restaurarVida();
        String tercero = m.ejecutarAccionTactica(atacante, objetivo);

        assertEquals("El Mago Merlín no tiene espacios de conjuro y lanza un truco de Descarga de Fuego.", tercero);
        assertFalse(tercero.contains("Tirada d20"), "El truco no resuelve tirada de ataque");
        assertEquals(100000, objetivo.getPuntosVidaActual(), "El truco no causa daño");
        assertEquals(0, m.getEspaciosConjuro(), "Los espacios no pueden quedar negativos");
    }

    @Test
    @DisplayName("Un Mago creado con 0 espacios solo lanza el truco")
    void magoSinEspaciosDesdeElInicio() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Merlín", 20, 10, 10, 10, 10, 16, 10, 10);
        Mago m = new Mago(1, 0);
        String log = m.ejecutarAccionTactica(atacante, AyudaPruebas.objetivo(10));
        assertTrue(log.contains("no tiene espacios de conjuro"));
        assertEquals(0, m.getEspaciosConjuro());
    }
}
