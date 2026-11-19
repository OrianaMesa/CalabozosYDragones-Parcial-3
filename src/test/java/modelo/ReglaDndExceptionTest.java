/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
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
class ReglaDndExceptionTest {

    @Test
    @DisplayName("Conserva el mensaje recibido")
    void conservaMensaje() {
        ReglaDndException ex = new ReglaDndException("Regla rota");
        assertEquals("Regla rota", ex.getMessage());
    }

    @Test
    @DisplayName("Es una excepción comprobada (checked), no RuntimeException")
    void esExcepcionComprobada() {
        assertTrue(Exception.class.isAssignableFrom(ReglaDndException.class));
        assertFalse(RuntimeException.class.isAssignableFrom(ReglaDndException.class));
    }

    @Test
    @DisplayName("Puede lanzarse y capturarse como ReglaDndException")
    void sePuedeLanzar() {
        ReglaDndException ex = assertThrows(ReglaDndException.class, () -> {
            throw new ReglaDndException("fallo");
        });
        assertEquals("fallo", ex.getMessage());
    }
}