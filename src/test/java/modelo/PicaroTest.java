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
 * @author orian
 */
class PicaroTest {

    @Test
    @DisplayName("Constructor: nombre de clase (con tilde), nivel y herramientas de ladrón")
    void constructor() throws ReglaDndException {
        Picaro p = new Picaro(4, true);
        assertAll(
                () -> assertEquals("Pícaro", p.getNombreClase()),
                () -> assertEquals(4, p.getNivel()),
                () -> assertTrue(p.isHerramientasLadron()),
                () -> assertFalse(new Picaro(1, false).isHerramientasLadron()));
    }

    @Test
    @DisplayName("Prerrequisito: Destreza >= 13 (límite 12/13)")
    void prerrequisito() throws ReglaDndException {
        Picaro p = new Picaro(1, true);
        assertFalse(p.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(18, 12, 10, 10, 10, 10)));
        assertTrue(p.cumplePrerrequisitoMulticlase(AyudaPruebas.heroeConAtributos(8, 13, 10, 10, 10, 10)));
    }

    @Test
    @DisplayName("Mensaje de prerrequisito y atributo especial")
    void textos() throws ReglaDndException {
        assertEquals("Pícaro requiere Destreza 13.", new Picaro(1, true).getMensajePrerrequisito());
        assertEquals("herramientasLadron: sí", new Picaro(1, true).getAtributoEspecial());
        assertEquals("herramientasLadron: no", new Picaro(1, false).getAtributoEspecial());
    }

    @Test
    @DisplayName("Acción: la frase indica si tiene herramientas de ladrón listas")
    void fraseSegunHerramientas() throws ReglaDndException {
        HeroeGremio atacante = AyudaPruebas.heroe("Vex", 25, 14, 10, 16, 10, 10, 10, 10);
        HeroeGremio objetivo = AyudaPruebas.objetivo(10);

        String con = new Picaro(3, true).ejecutarAccionTactica(atacante, objetivo);
        String sin = new Picaro(3, false).ejecutarAccionTactica(atacante, objetivo);

        assertTrue(con.startsWith("El Pícaro Vex aprovecha la ventaja táctica"), con);
        assertTrue(con.contains("[Herramientas de ladrón listas]."));
        assertFalse(sin.contains("Herramientas de ladrón"));
    }

    @Test
    @DisplayName("Acción: dados de Ataque Furtivo = max(1, (nivel + 1) / 2) d6, usando Destreza")
    void dadosFurtivosSegunNivel() throws ReglaDndException {
        // nivel → dados: 1→1, 2→1, 3→2, 4→2, 5→3, 20→10
        int[][] niveles = {{1, 1}, {2, 1}, {3, 2}, {4, 2}, {5, 3}, {20, 10}};
        for (int[] caso : niveles) {
            int nivel = caso[0];
            int dados = caso[1];
            HeroeGremio atacante = AyudaPruebas.heroe("Vex", 25, 14, 10, 16, 10, 10, 10, 10); // des 16 → +3
            Picaro p = new Picaro(nivel, true);
            atacante.agregarClase(p);

            AyudaPruebas.ejecutarYVerificar(p, atacante, AyudaPruebas.objetivo(-100), 150,
                    3, dados, 6, "Ataque Furtivo");
            AyudaPruebas.ejecutarYVerificar(p, atacante, AyudaPruebas.objetivo(100), 150,
                    3, dados, 6, "Ataque Furtivo");
        }
    }
}
