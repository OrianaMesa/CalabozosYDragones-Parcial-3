/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
/**
 *
 * @author orian
 */
class DadoD20Test {

    @Test
    @DisplayName("lanzar() siempre devuelve un valor entre 1 y 20 y alcanza todas las caras")
    void lanzarDevuelveValoresEntre1y20() {
        boolean[] vistos = new boolean[DadoD20.MAX_CARAS + 1];
        for (int i = 0; i < 5000; i++) {
            int r = DadoD20.lanzar();
            assertTrue(r >= 1 && r <= 20, "Valor fuera de rango: " + r);
            vistos[r] = true;
        }
        for (int cara = 1; cara <= DadoD20.MAX_CARAS; cara++) {
            assertTrue(vistos[cara], "La cara " + cara + " nunca salió en 5000 tiradas");
        }
    }

    @Test
    @DisplayName("lanzar(mod) suma el modificador positivo a la tirada")
    void lanzarConModificadorPositivo() {
        for (int i = 0; i < 2000; i++) {
            int r = DadoD20.lanzar(5);
            assertTrue(r >= 6 && r <= 25, "Valor fuera de rango [6,25]: " + r);
        }
    }

    @Test
    @DisplayName("lanzar(mod) resta el modificador negativo a la tirada")
    void lanzarConModificadorNegativo() {
        for (int i = 0; i < 2000; i++) {
            int r = DadoD20.lanzar(-3);
            assertTrue(r >= -2 && r <= 17, "Valor fuera de rango [-2,17]: " + r);
        }
    }

    @Test
    @DisplayName("lanzar(0) equivale a una tirada pura de d20")
    void lanzarConModificadorCero() {
        for (int i = 0; i < 1000; i++) {
            int r = DadoD20.lanzar(0);
            assertTrue(r >= 1 && r <= 20, "Valor fuera de rango: " + r);
        }
    }

    @Test
    @DisplayName("lanzarDanio respeta el rango NdC+mod y alcanza ambos extremos")
    void lanzarDanioRespetaRango() {
        int minimo = Integer.MAX_VALUE;
        int maximo = Integer.MIN_VALUE;
        for (int i = 0; i < 20000; i++) {
            int d = DadoD20.lanzarDanio(2, 6, 3);
            assertTrue(d >= 5 && d <= 15, "Daño fuera de rango [5,15]: " + d);
            minimo = Math.min(minimo, d);
            maximo = Math.max(maximo, d);
        }
        assertEquals(5, minimo, "2d6+3 debería poder dar 5");
        assertEquals(15, maximo, "2d6+3 debería poder dar 15");
    }

    @Test
    @DisplayName("lanzarDanio con dados de 1 cara es determinista")
    void lanzarDanioDeterminista() {
        assertEquals(5, DadoD20.lanzarDanio(3, 1, 2));
        assertEquals(3, DadoD20.lanzarDanio(3, 1, 0));
    }

    @Test
    @DisplayName("lanzarDanio nunca devuelve menos de 1")
    void lanzarDanioMinimoUno() {
        for (int i = 0; i < 1000; i++) {
            assertEquals(1, DadoD20.lanzarDanio(1, 6, -10));
        }
        assertEquals(1, DadoD20.lanzarDanio(2, 1, -5));
    }

    @Test
    @DisplayName("lanzarDanio con 0 dados devuelve solo el modificador (mínimo 1)")
    void lanzarDanioSinDados() {
        assertEquals(5, DadoD20.lanzarDanio(0, 6, 5));
        assertEquals(1, DadoD20.lanzarDanio(0, 6, 0));
        assertEquals(1, DadoD20.lanzarDanio(0, 6, -3));
    }

    @Test
    @DisplayName("esCritico solo es verdadero con un 20 natural")
    void esCriticoSoloConVeinte() {
        for (int n = 1; n <= 19; n++) {
            assertFalse(DadoD20.esCritico(n), "No debería ser crítico: " + n);
        }
        assertTrue(DadoD20.esCritico(20));
        assertFalse(DadoD20.esCritico(21));
        assertFalse(DadoD20.esCritico(0));
    }

    @Test
    @DisplayName("esPifia solo es verdadero con un 1 natural")
    void esPifiaSoloConUno() {
        assertTrue(DadoD20.esPifia(1));
        for (int n = 2; n <= 20; n++) {
            assertFalse(DadoD20.esPifia(n), "No debería ser pifia: " + n);
        }
        assertFalse(DadoD20.esPifia(0));
    }

    @Test
    @DisplayName("MAX_CARAS vale 20 y el constructor es privado (clase de utilidad)")
    void constantesYConstructorPrivado() throws Exception {
        assertEquals(20, DadoD20.MAX_CARAS);
        Constructor<DadoD20> ctor = DadoD20.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()), "El constructor debe ser privado");
    }
}
