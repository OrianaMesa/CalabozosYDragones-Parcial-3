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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
/**
 *
 * @author orian
 */
class AventureroTest {

    private HeroeGremio heroe;

    @BeforeEach
    void preparar() throws ReglaDndException {
        // fue 16, des 14, con 14, int 10, sab 12, car 8
        heroe = AyudaPruebas.heroe("Thorin", 40, 15, 16, 14, 14, 10, 12, 8);
    }

    // ------------------------------------------------------------------ constructor

    @Test
    @DisplayName("El constructor guarda los valores y empieza con la vida al máximo")
    void constructorValoresIniciales() {
        assertAll(
                () -> assertEquals("Thorin", heroe.getNombre()),
                () -> assertEquals(40, heroe.getPuntosVidaMax()),
                () -> assertEquals(40, heroe.getPuntosVidaActual()),
                () -> assertEquals(15, heroe.getClaseArmadura()),
                () -> assertEquals(16, heroe.getFuerza()),
                () -> assertEquals(14, heroe.getDestreza()),
                () -> assertEquals(10, heroe.getInteligencia()),
                () -> assertEquals(12, heroe.getSabiduria()),
                () -> assertEquals(8, heroe.getCarisma()),
                () -> assertTrue(heroe.getClases().isEmpty()),
                () -> assertEquals(0, heroe.getNivelTotal()),
                () -> assertTrue(heroe.estaVivo()));
    }

    @Test
    @DisplayName("El nombre se recorta (trim)")
    void nombreSeRecorta() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("  Elara  ", 10, 10, 10, 10, 10, 10, 10, 10);
        assertEquals("Elara", h.getNombre());
    }

    @Test
    @DisplayName("Un nombre nulo, vacío o en blanco lanza ReglaDndException")
    void nombreInvalido() {
        String[] invalidos = {null, "", "   "};
        for (String nombre : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class,
                    () -> AyudaPruebas.heroe(nombre, 10, 10, 10, 10, 10, 10, 10, 10));
            assertEquals("El nombre del aventurero no puede estar vacío.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Puntos de vida 0 o negativos lanzan ReglaDndException")
    void puntosVidaInvalidos() {
        int[] invalidos = {0, -1, -50};
        for (int pv : invalidos) {
            ReglaDndException ex = assertThrows(ReglaDndException.class,
                    () -> AyudaPruebas.heroe("Test", pv, 10, 10, 10, 10, 10, 10, 10));
            assertEquals("Los puntos de vida deben ser mayores a 0.", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Con 1 PG el constructor es válido (límite inferior)")
    void puntosVidaMinimoValido() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroe("Frágil", 1, 10, 10, 10, 10, 10, 10, 10);
        assertEquals(1, h.getPuntosVidaMax());
        assertTrue(h.estaVivo());
    }

    @Test
    @DisplayName("getTitulo() por defecto de Aventurero es 'Aventurero del Gremio'")
    void tituloPorDefecto() throws ReglaDndException {
        Aventurero anonimo = new Aventurero("Anon", 10, 10, 10, 10, 10, 10, 10, 10) {
            @Override
            public String ejecutarTurnoCompleto(Aventurero objetivo) {
                return "";
            }
        };
        assertEquals("Aventurero del Gremio", anonimo.getTitulo());
    }

    // ------------------------------------------------------------------ modificadores

    @Test
    @DisplayName("Cada modificador se calcula con su propio atributo: floor((valor - 10) / 2)")
    void modificadoresPorAtributo() throws ReglaDndException {
        // fue 8, des 12, con 14, int 16, sab 10, car 18
        HeroeGremio h = AyudaPruebas.heroeConAtributos(8, 12, 14, 16, 10, 18);
        assertAll(
                () -> assertEquals(-1, h.getModFuerza()),
                () -> assertEquals(1, h.getModDestreza()),
                () -> assertEquals(2, h.getModConstitucion()),
                () -> assertEquals(3, h.getModInteligencia()),
                () -> assertEquals(0, h.getModSabiduria()),
                () -> assertEquals(4, h.getModCarisma()));
    }

    @Test
    @DisplayName("Tabla de modificadores: redondeo hacia abajo, incluidos los valores bajos")
    void tablaDeModificadores() throws ReglaDndException {
        int[][] tabla = {
            {1, -5}, {7, -2}, {8, -1}, {9, -1}, {10, 0}, {11, 0},
            {12, 1}, {13, 1}, {15, 2}, {18, 4}, {20, 5}, {30, 10}
        };
        for (int[] fila : tabla) {
            int valor = fila[0];
            int esperado = fila[1];
            HeroeGremio h = AyudaPruebas.heroeConAtributos(valor, valor, valor, valor, valor, valor);
            assertAll("Atributo " + valor,
                    () -> assertEquals(esperado, h.getModFuerza(), "Fuerza " + valor),
                    () -> assertEquals(esperado, h.getModDestreza(), "Destreza " + valor),
                    () -> assertEquals(esperado, h.getModConstitucion(), "Constitución " + valor),
                    () -> assertEquals(esperado, h.getModInteligencia(), "Inteligencia " + valor),
                    () -> assertEquals(esperado, h.getModSabiduria(), "Sabiduría " + valor),
                    () -> assertEquals(esperado, h.getModCarisma(), "Carisma " + valor));
        }
    }

    // ------------------------------------------------------------------ bono de competencia

    @Test
    @DisplayName("Sin clases el bono de competencia es +2")
    void bonoCompetenciaSinClases() {
        assertEquals(2, heroe.getBonoCompetencia());
    }

    @Test
    @DisplayName("El bono de competencia sube cada 4 niveles: 1-4→+2, 5-8→+3, 9-12→+4, 13-16→+5, 17-20→+6")
    void bonoCompetenciaPorNivel() throws ReglaDndException {
        int[][] casos = {
            {1, 2}, {4, 2}, {5, 3}, {8, 3}, {9, 4}, {12, 4}, {13, 5}, {16, 5}, {17, 6}, {20, 6}
        };
        for (int[] caso : casos) {
            HeroeGremio h = AyudaPruebas.heroeVeterano();
            h.agregarClase(new Guerrero(caso[0], true));
            assertEquals(caso[1], h.getBonoCompetencia(), "Nivel total " + caso[0]);
        }
    }

    // ------------------------------------------------------------------ iniciativa

    @Test
    @DisplayName("tirarIniciativa = d20 + modificador de Destreza (mod positivo)")
    void iniciativaConModPositivo() {
        // des 14 → mod +2 → rango [3, 22]
        for (int i = 0; i < 1000; i++) {
            int r = heroe.tirarIniciativa();
            assertTrue(r >= 3 && r <= 22, "Iniciativa fuera de rango [3,22]: " + r);
        }
    }

    @Test
    @DisplayName("tirarIniciativa = d20 + modificador de Destreza (mod negativo)")
    void iniciativaConModNegativo() throws ReglaDndException {
        // des 6 → mod -2 → rango [-1, 18]
        HeroeGremio torpe = AyudaPruebas.heroeConAtributos(10, 6, 10, 10, 10, 10);
        for (int i = 0; i < 1000; i++) {
            int r = torpe.tirarIniciativa();
            assertTrue(r >= -1 && r <= 18, "Iniciativa fuera de rango [-1,18]: " + r);
        }
    }

    // ------------------------------------------------------------------ vida

    @Test
    @DisplayName("recibirDanio reduce los PG actuales sin tocar el máximo")
    void recibirDanioReduceVida() {
        heroe.recibirDanio(15);
        assertEquals(25, heroe.getPuntosVidaActual());
        assertEquals(40, heroe.getPuntosVidaMax());
        assertTrue(heroe.estaVivo());
    }

    @Test
    @DisplayName("recibirDanio igual a los PG deja al aventurero en 0 y fuera de combate")
    void recibirDanioExacto() {
        heroe.recibirDanio(40);
        assertEquals(0, heroe.getPuntosVidaActual());
        assertFalse(heroe.estaVivo());
    }

    @Test
    @DisplayName("recibirDanio nunca deja los PG por debajo de 0")
    void recibirDanioNoBajaDeCero() {
        heroe.recibirDanio(1000);
        assertEquals(0, heroe.getPuntosVidaActual());
        assertFalse(heroe.estaVivo());
    }

    @Test
    @DisplayName("recibirDanio(0) no cambia la vida")
    void recibirDanioCero() {
        heroe.recibirDanio(0);
        assertEquals(40, heroe.getPuntosVidaActual());
    }

    @Test
    @DisplayName("Con 1 PG sigue vivo")
    void conUnPgSigueVivo() {
        heroe.recibirDanio(39);
        assertEquals(1, heroe.getPuntosVidaActual());
        assertTrue(heroe.estaVivo());
    }

    @Test
    @DisplayName("curar suma PG pero nunca supera el máximo")
    void curarNoSuperaMaximo() {
        heroe.recibirDanio(15);
        heroe.curar(10);
        assertEquals(35, heroe.getPuntosVidaActual());
        heroe.curar(100);
        assertEquals(40, heroe.getPuntosVidaActual());
    }

    @Test
    @DisplayName("Un aventurero caído (0 PG) puede curarse y volver a estar vivo")
    void curarRevive() {
        heroe.recibirDanio(100);
        assertFalse(heroe.estaVivo());
        heroe.curar(5);
        assertEquals(5, heroe.getPuntosVidaActual());
        assertTrue(heroe.estaVivo());
    }

    @Test
    @DisplayName("restaurarVida devuelve los PG al máximo")
    void restaurarVida() {
        heroe.recibirDanio(100);
        heroe.restaurarVida();
        assertEquals(40, heroe.getPuntosVidaActual());
        assertTrue(heroe.estaVivo());
    }

    // ------------------------------------------------------------------ agregarClase

    @Test
    @DisplayName("agregarClase añade la clase y getNivelTotal suma los niveles")
    void agregarClasesSumaNiveles() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(5, true));
        h.agregarClase(new Mago(3, 4));
        assertEquals(2, h.getClases().size());
        assertEquals(8, h.getNivelTotal());
    }

    @Test
    @DisplayName("No se puede repetir una clase ya asignada")
    void noSePuedeRepetirClase() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(3, true));
        ReglaDndException ex = assertThrows(ReglaDndException.class,
                () -> h.agregarClase(new Guerrero(2, false)));
        assertTrue(ex.getMessage().contains("Guerrero"));
        assertTrue(ex.getMessage().contains("prohibido repetir clase"));
        assertEquals(1, h.getClases().size());
        assertEquals(3, h.getNivelTotal());
    }

    @Test
    @DisplayName("Máximo 4 clases por aventurero")
    void maximoCuatroClases() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(1, true));
        h.agregarClase(new Mago(1, 3));
        h.agregarClase(new Clerigo(1, "Pelor"));
        h.agregarClase(new Bardo(1, "Laúd"));

        ReglaDndException ex = assertThrows(ReglaDndException.class,
                () -> h.agregarClase(new Picaro(1, true)));
        assertEquals("Un aventurero no puede tener más de 4 clases.", ex.getMessage());
        assertEquals(4, h.getClases().size());
    }

    @Test
    @DisplayName("El nivel total no puede superar 20, pero 20 exactos es válido")
    void nivelTotalMaximo() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(15, true));

        ReglaDndException ex = assertThrows(ReglaDndException.class,
                () -> h.agregarClase(new Mago(6, 3)));
        assertEquals("El nivel total (21) excede el máximo permitido de 20.", ex.getMessage());
        assertEquals(15, h.getNivelTotal());

        h.agregarClase(new Mago(5, 3));
        assertEquals(20, h.getNivelTotal());

        assertThrows(ReglaDndException.class, () -> h.agregarClase(new Clerigo(1, "Pelor")));
        assertEquals(20, h.getNivelTotal());
    }

    @Test
    @DisplayName("La primera clase se acepta aunque el aventurero no cumpla su prerrequisito")
    void primeraClaseSinPrerrequisito() throws ReglaDndException {
        HeroeGremio debil = AyudaPruebas.heroeConAtributos(8, 8, 8, 8, 8, 8);
        debil.agregarClase(new Mago(1, 2));
        assertEquals(1, debil.getClases().size());
    }

    @Test
    @DisplayName("Multiclase: falla si no cumple el prerrequisito de la clase inicial")
    void multiclaseFallaPorClaseInicial() throws ReglaDndException {
        // Fuerza 15 (sirve para Guerrero) pero Inteligencia 8 (no sirve para Mago, la clase inicial)
        HeroeGremio h = AyudaPruebas.heroeConAtributos(15, 8, 10, 8, 10, 10);
        h.agregarClase(new Mago(1, 2));

        ReglaDndException ex = assertThrows(ReglaDndException.class,
                () -> h.agregarClase(new Guerrero(1, true)));
        assertTrue(ex.getMessage().contains("clase inicial"));
        assertTrue(ex.getMessage().contains("Mago requiere Inteligencia 13."));
        assertEquals(1, h.getClases().size());
    }

    @Test
    @DisplayName("Multiclase: falla si no cumple el prerrequisito de la nueva clase")
    void multiclaseFallaPorNuevaClase() throws ReglaDndException {
        // Inteligencia 15 (Mago ok) pero Fuerza 8 y Destreza 8 (Guerrero no)
        HeroeGremio h = AyudaPruebas.heroeConAtributos(8, 8, 10, 15, 10, 10);
        h.agregarClase(new Mago(1, 2));

        ReglaDndException ex = assertThrows(ReglaDndException.class,
                () -> h.agregarClase(new Guerrero(1, true)));
        assertTrue(ex.getMessage().contains("No cumple prerrequisito para multiclase"));
        assertTrue(ex.getMessage().contains("Guerrero requiere Fuerza 13 o Destreza 13."));
        assertEquals(1, h.getClases().size());
    }

    @Test
    @DisplayName("Multiclase válida cuando se cumplen ambos prerrequisitos")
    void multiclaseValida() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeConAtributos(15, 10, 10, 15, 10, 10);
        h.agregarClase(new Guerrero(5, true));
        h.agregarClase(new Mago(3, 4));
        assertEquals("Guerrero Nv.5 / Mago Nv.3", h.getResumenClases());
    }

    @Test
    @DisplayName("getClases() devuelve una vista no modificable")
    void getClasesNoModificable() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(1, true));
        assertThrows(UnsupportedOperationException.class,
                () -> h.getClases().add(new Mago(1, 1)));
        assertThrows(UnsupportedOperationException.class, () -> h.getClases().clear());
    }

    // ------------------------------------------------------------------ textos

    @Test
    @DisplayName("getResumenClases sin clases es una cadena vacía")
    void resumenSinClases() {
        assertEquals("", heroe.getResumenClases());
    }

    @Test
    @DisplayName("getResumenClases une las clases con ' / ' y su nivel")
    void resumenConVariasClases() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Picaro(2, true));
        h.agregarClase(new Bardo(4, "Flauta"));
        h.agregarClase(new Clerigo(6, "Tyr"));
        assertEquals("Pícaro Nv.2 / Bardo Nv.4 / Clérigo Nv.6", h.getResumenClases());
    }

    @Test
    @DisplayName("getAtributoEspecial sin clases es una cadena vacía")
    void atributoEspecialSinClases() {
        assertEquals("", heroe.getAtributoEspecial());
    }

    @Test
    @DisplayName("getAtributoEspecial combina el atributo especial de cada clase separado por '; '")
    void atributoEspecialCombinado() throws ReglaDndException {
        HeroeGremio h = AyudaPruebas.heroeVeterano();
        h.agregarClase(new Guerrero(3, true));
        h.agregarClase(new Mago(2, 4));
        assertEquals("Guerrero (armaduraPesada: sí); Mago (espaciosConjuro: 4)", h.getAtributoEspecial());
    }
}
