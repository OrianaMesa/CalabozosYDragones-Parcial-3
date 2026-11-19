/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author oriana
 */
class GuerreroTest {

    @Test
    @DisplayName("Constructor: nombre de clase, nivel y armadura pesada")
    void constructor() throws ReglaDndException {
        Guerrero g = new Guerrero(5, true);
        assertAll(
                () -> assertEquals("Guerrero", g.getNombreClase()),
                () -> assertEquals(5, g.getNivel()),
                () -> assertTrue(g.isArmaduraPesada()),
                () -> assertFalse(new Guerrero(1, false).isArmaduraPesada()));
    }

    @Test
    @DisplayName("Prerrequisito: Fuerza >= 13 o Destreza >= 13")
    void prerrequisito() throws ReglaDndException {
        Guerrero g = new Guerrero(1, true);
        assertAll(
                () -> assertTrue(g.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(13, 8, 10, 10, 10, 10)),
                        "Solo Fuerza 13"),
                () -> assertTrue(g.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(8, 13, 10, 10, 10, 10)),
                        "Solo Destreza 13"),
                () -> assertTrue(g.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(15, 15, 10, 10, 10, 10)),
                        "Ambas altas"),
                () -> assertFalse(g.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(12, 12, 10, 10, 10, 10)),
                        "Ambas en 12"));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito")
    void mensajePrerrequisito() throws ReglaDndException {
        assertEquals("Guerrero requiere Fuerza 13 o Destreza 13.", new Guerrero(1, true).getMensajePrerrequisito());
    }

    @Test
    @DisplayName("Atributo especial refleja si usa armadura pesada")
    void atributoEspecial() throws ReglaDndException {
        assertEquals("armaduraPesada: sí", new Guerrero(1, true).getAtributoEspecial());
        assertEquals("armaduraPesada: no", new Guerrero(1, false).getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: la frase depende de la armadura pesada")
    void fraseSegunArmadura() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Thorin", 40, 15, 16, 10, 10, 10, 10, 10);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String pesada = new Guerrero(3, true).ejecutarAccionTactica(atacante, objetivo);
        String agil = new Guerrero(3, false).ejecutarAccionTactica(atacante, objetivo);

        assertTrue(pesada.startsWith("El Guerrero Thorin usa Oleada de Acción"));
        assertTrue(pesada.contains("(protegido con Armadura Pesada)."));
        assertTrue(agil.contains("(estilo ágil)."));
    }

    @Test
    @DisplayName("Acción: usa el mayor modificador entre Fuerza y Destreza (Destreza mayor)")
    void usaMejorModificadorDestreza() throws ReglaDndException {
        // fue 8 (-1), des 18 (+4) → +4
        HeroeGremio atacante = AyudaPruebas.heroe("Legolas", 40, 15, 8, 18, 10, 10, 10, 10);
        Guerrero g = new Guerrero(5, false);
        atacante.agregarClase(g);
        AyudaPruebas.ejecutarYVerificar(g, atacante, AyudaPruebas.objetivo(-100), 300, 4, 2, 8, "Oleada de Acción");
        AyudaPruebas.ejecutarYVerificar(g, atacante, AyudaPruebas.objetivo(100), 300, 4, 2, 8, "Oleada de Acción");
    }

    @Test
    @DisplayName("Acción: usa el mayor modificador entre Fuerza y Destreza (Fuerza mayor)")
    void usaMejorModificadorFuerza() throws ReglaDndException {
        // fue 18 (+4), des 8 (-1) → +4
        HeroeGremio atacante = AyudaPruebas.heroe("Conan", 40, 15, 18, 8, 10, 10, 10, 10);
        Guerrero g = new Guerrero(5, true);
        atacante.agregarClase(g);
        AyudaPruebas.ejecutarYVerificar(g, atacante, AyudaPruebas.objetivo(-100), 300, 4, 2, 8, "Oleada de Acción");
        AyudaPruebas.ejecutarYVerificar(g, atacante, AyudaPruebas.objetivo(100), 300, 4, 2, 8, "Oleada de Acción");
    }
}
