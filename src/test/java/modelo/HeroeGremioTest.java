/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author orian
 */
class HeroeGremioTest {

    // ------------------------------------------------------------------ título

    @Test
    @DisplayName("Si el título es nulo se usa el título por defecto")
    void tituloNuloUsaPorDefecto() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Ana", 10, 10, 10, 10, 10, 10, 10, 10, null);
        assertEquals("Aventurero de Costa de la Espada", h.getTituloGremio());
        assertEquals("Aventurero de Costa de la Espada", h.getTitulo());
    }

    @Test
    @DisplayName("Si el título está vacío o en blanco se usa el título por defecto")
    void tituloVacioUsaPorDefecto() throws ReglaDndException {
        HeroeGremio vacio = AyudaPruebas.heroe("Ana", 10, 10, 10, 10, 10, 10, 10, 10, "");
        HeroeGremio blanco = AyudaPruebas.heroe("Ana", 10, 10, 10, 10, 10, 10, 10, 10, "   ");
        assertEquals("Aventurero de Costa de la Espada", vacio.getTitulo());
        assertEquals("Aventurero de Costa de la Espada", blanco.getTitulo());
    }

    @Test
    @DisplayName("Un título válido se guarda recortado y getTitulo() lo devuelve")
    void tituloValido() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Ana", 10, 10, 10, 10, 10, 10, 10, 10, "  Campeona del Norte  ");
        assertEquals("Campeona del Norte", h.getTituloGremio());
        assertEquals("Campeona del Norte", h.getTitulo());
    }

    @Test
    @DisplayName("setTituloGremio cambia el título")
    void setTitulo() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Ana", 10, 10, 10, 10, 10, 10, 10, 10, "Novata");
        h.setTituloGremio("Veterana");
        assertEquals("Veterana", h.getTituloGremio());
        assertEquals("Veterana", h.getTitulo());
    }

    // ------------------------------------------------------------------ toString

    @Test
    @DisplayName("toString muestra nombre, título, nivel total, clases, PG y CA")
    void toStringCompleto() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Thorin", 40, 15, 16, 16, 16, 16, 16, 16, "Campeón");
        h.agregarClase(new Guerrero(5, true));
        h.agregarClase(new Mago(3, 4));
        h.recibirDanio(10);
        assertEquals("Thorin (Campeón) | Nv.Total: 8 | Clases: [Guerrero Nv.5 / Mago Nv.3] | PG: 30/40 | CA: 15",
                h.toString());
    }

    @Test
    @DisplayName("toString sin clases muestra nivel total 0 y corchetes vacíos")
    void toStringSinClases() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Ana", 12, 11, 10, 10, 10, 10, 10, 10, "Novata");
        assertEquals("Ana (Novata) | Nv.Total: 0 | Clases: [] | PG: 12/12 | CA: 11", h.toString());
    }

    // ------------------------------------------------------------------ turno completo

    @Test
    @DisplayName("El turno completo tiene encabezado y una acción por cada clase")
    void turnoUnaAccionPorClase() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        atacante.agregarClase(new Guerrero(3, true));
        atacante.agregarClase(new Mago(2, 5));
        HeroeGremio objetivo = AyudaPruebas.objetivo(-100);

        String log = atacante.ejecutarTurnoCompleto(objetivo);

        assertTrue(log.startsWith(">>> Turno de Thorin [Guerrero Nv.3 / Mago Nv.2 | Nv. Total 5 | PG 40/40]:\n"),
                "Encabezado inesperado: " + log);
        assertEquals(2, AyudaPruebas.contar(log, "  * "), "Debe haber una acción por clase");
        assertTrue(log.contains("El Guerrero Thorin"));
        assertTrue(log.contains("El Mago Thorin"));
    }

    @Test
    @DisplayName("Las acciones se ejecutan en el orden en que se agregaron las clases")
    void turnoRespetaOrdenDeClases() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        atacante.agregarClase(new Picaro(2, true));
        atacante.agregarClase(new Bardo(2, "Laúd"));
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String log = atacante.ejecutarTurnoCompleto(objetivo);

        int posPicaro = log.indexOf("El Pícaro Thorin");
        int posBardo = log.indexOf("El Bardo Thorin");
        assertTrue(posPicaro >= 0 && posBardo >= 0);
        assertTrue(posPicaro < posBardo, "El Pícaro debe actuar antes que el Bardo");
    }

    @Test
    @DisplayName("Si el objetivo ya está caído no se ejecuta ninguna acción (ni se gastan recursos)")
    void turnoContraObjetivoCaido() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        Mago mago = new Mago(2, 5);
        atacante.agregarClase(new Guerrero(3, true));
        atacante.agregarClase(mago);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);
        objetivo.recibirDanio(Integer.MAX_VALUE);
        assertFalse(objetivo.estaVivo());

        String log = atacante.ejecutarTurnoCompleto(objetivo);

        assertTrue(log.startsWith(">>> Turno de Thorin"));
        assertEquals(0, AyudaPruebas.contar(log, "  * "));
        assertEquals(5, mago.getEspaciosConjuro(), "No debe gastarse ningún espacio de conjuro");
    }

    @Test
    @DisplayName("Sin clases el turno solo contiene el encabezado")
    void turnoSinClases() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String log = atacante.ejecutarTurnoCompleto(objetivo);

        assertTrue(log.contains("Nv. Total 0"));
        assertEquals(0, AyudaPruebas.contar(log, "  * "));
        assertEquals(100000, objetivo.getPuntosVidaActual());
    }

    @Test
    @DisplayName("El turno se interrumpe en cuanto el objetivo cae")
    void turnoSeDetieneCuandoElObjetivoCae() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        atacante.agregarClase(new Guerrero(2, true));
        atacante.agregarClase(new Picaro(2, true));
        atacante.agregarClase(new Bardo(1, "Laúd"));

        boolean huboInterrupcion = false;
        for (int i = 0; i < 200; i++) {
            HeroeGremio objetivo = AyudaPruebas.heroe("Dummy", 1, -100, 10, 10, 10, 10, 10, 10);
            String log = atacante.ejecutarTurnoCompleto(objetivo);
            int acciones = AyudaPruebas.contar(log, "  * ");

            assertTrue(acciones >= 1 && acciones <= 3, "Acciones fuera de rango: " + acciones);
            if (acciones < 3) {
                huboInterrupcion = true;
                assertFalse(objetivo.estaVivo(), "Solo puede cortarse el turno si el objetivo cayó");
            }
        }
        assertTrue(huboInterrupcion, "Con 1 PG el turno debió interrumpirse alguna vez");
    }

    @Test
    @DisplayName("Con un objetivo muy resistente se ejecutan siempre todas las acciones")
    void turnoCompletoContraObjetivoResistente() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroeVeterano();
        atacante.agregarClase(new Guerrero(2, true));
        atacante.agregarClase(new Picaro(2, true));
        atacante.agregarClase(new Bardo(1, "Laúd"));
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        for (int i = 0; i < 100; i++) {
            String log = atacante.ejecutarTurnoCompleto(objetivo);
            assertEquals(3, AyudaPruebas.contar(log, "  * "));
        }
    }
}
