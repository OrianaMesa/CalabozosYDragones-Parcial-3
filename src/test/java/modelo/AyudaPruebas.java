/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author oriana
 */
final class AyudaPruebas {

    /** Formato del registro: "[natural] + bono = total vs CA x". */
    private static final Pattern TIRADA =
            Pattern.compile("\\[(\\d+)\\] \\+ (-?\\d+) = (-?\\d+) vs CA (-?\\d+)");

    private AyudaPruebas() { }

    static HeroeGremio heroe(String nombre, int pv, int ca, int fue, int des, int con,
                             int inte, int sab, int car, String titulo) throws ReglaDndException {
        return new HeroeGremio(nombre, pv, ca, fue, des, con, inte, sab, car, titulo);
    }

    static HeroeGremio heroe(String nombre, int pv, int ca, int fue, int des, int con,
                             int inte, int sab, int car) throws ReglaDndException {
        return heroe(nombre, pv, ca, fue, des, con, inte, sab, car, "Tester");
    }

    /** Héroe con los atributos indicados (30 PG, CA 12). */
    static HeroeGremio heroeConAtributos(int fue, int des, int con, int inte, int sab, int car)
            throws ReglaDndException {
        return heroe("Tester", 30, 12, fue, des, con, inte, sab, car);
    }

    /** Héroe con todos los atributos en 16: cumple el prerrequisito de cualquier clase. */
    static HeroeGremio heroeVeterano() throws ReglaDndException {
        return heroe("Thorin", 40, 15, 16, 16, 16, 16, 16, 16);
    }

    /** Objetivo con muchísima vida y la CA indicada, para recibir golpes sin morir. */
    static HeroeGremio objetivo(int ca) throws ReglaDndException {
        return heroe("Dummy", 100000, ca, 10, 10, 10, 10, 10, 10);
    }

    static int contar(String texto, String fragmento) {
        int cuenta = 0;
        int desde = 0;
        while ((desde = texto.indexOf(fragmento, desde)) >= 0) {
            cuenta++;
            desde += fragmento.length();
        }
        return cuenta;
    }

    /**
     * Verifica un registro de ataque producido por ClasePersonaje.resolverAtaqueD20.
     *
     * @return PIFIA, CRITICO, IMPACTO o FALLO según el resultado detectado.
     */
    static String verificarAtaque(String log, Aventurero atacante, Aventurero objetivo, int pvAntes,
                                  int modAtributo, int dados, int caras, String habilidad) {
        Matcher m = TIRADA.matcher(log);
        assertTrue(m.find(), "El registro debe incluir la tirada d20: " + log);

        int natural = Integer.parseInt(m.group(1));
        int bono = Integer.parseInt(m.group(2));
        int total = Integer.parseInt(m.group(3));
        int ca = Integer.parseInt(m.group(4));

        assertTrue(natural >= 1 && natural <= 20, "La tirada natural debe estar entre 1 y 20: " + natural);
        assertEquals(modAtributo + atacante.getBonoCompetencia(), bono,
                "El bono debe ser modificador del atributo + bono de competencia");
        assertEquals(natural + bono, total, "El total debe ser tirada natural + bono");
        assertEquals(objetivo.getClaseArmadura(), ca, "Debe compararse contra la CA del objetivo");

        int danio = pvAntes - objetivo.getPuntosVidaActual();

        if (natural == 1) {
            assertTrue(log.contains("PIFIA"), "Un 1 natural debe ser pifia: " + log);
            assertEquals(0, danio, "Una pifia no causa daño");
            return "PIFIA";
        }
        if (natural == 20) {
            int min = Math.max(1, dados * 2 + modAtributo);
            int max = Math.max(1, dados * 2 * caras + modAtributo);
            assertTrue(log.contains("GOLPE CRÍTICO"), "Un 20 natural debe ser crítico: " + log);
            assertTrue(danio >= min && danio <= max,
                    "Daño crítico fuera de rango [" + min + "," + max + "]: " + danio);
            verificarDanioEnRegistro(log, objetivo, danio, habilidad);
            return "CRITICO";
        }
        if (total >= ca) {
            int min = Math.max(1, dados + modAtributo);
            int max = Math.max(1, dados * caras + modAtributo);
            assertTrue(log.contains("IMPACTO"), "Total >= CA debe ser impacto: " + log);
            assertTrue(danio >= min && danio <= max,
                    "Daño fuera de rango [" + min + "," + max + "]: " + danio);
            verificarDanioEnRegistro(log, objetivo, danio, habilidad);
            return "IMPACTO";
        }
        assertTrue(log.contains("FALLO"), "Total < CA debe ser fallo: " + log);
        assertEquals(0, danio, "Un fallo no causa daño");
        return "FALLO";
    }

    private static void verificarDanioEnRegistro(String log, Aventurero objetivo, int danio, String habilidad) {
        assertTrue(log.contains(habilidad), "El registro debe nombrar la habilidad " + habilidad);
        assertTrue(log.contains("-" + danio + " PG a " + objetivo.getNombre()),
                "El registro debe indicar el daño real aplicado: " + log);
        assertTrue(log.contains("PG restantes: " + objetivo.getPuntosVidaActual() + ")"),
                "El registro debe indicar los PG restantes reales: " + log);
    }

    /**
     * Ejecuta la acción táctica muchas veces y verifica cada resultado.
     * Con CA muy baja (<= -50) exige que haya impactos; con CA muy alta (>= 50)
     * exige que no haya impactos normales (solo críticos) y que haya fallos.
     */
    static void ejecutarYVerificar(ClasePersonaje clase, Aventurero atacante, Aventurero objetivo,
                                   int iteraciones, int modAtributo, int dados, int caras, String habilidad) {
        int impactos = 0;
        int fallos = 0;
        for (int i = 0; i < iteraciones; i++) {
            objetivo.restaurarVida();
            int pvAntes = objetivo.getPuntosVidaActual();
            String log = clase.ejecutarAccionTactica(atacante, objetivo);
            String resultado = verificarAtaque(log, atacante, objetivo, pvAntes,
                    modAtributo, dados, caras, habilidad);
            if (resultado.equals("IMPACTO")) {
                impactos++;
            } else if (resultado.equals("FALLO")) {
                fallos++;
            }
        }
        if (objetivo.getClaseArmadura() <= -50) {
            assertTrue(impactos > 0, "Con CA muy baja debe haber impactos");
            assertEquals(0, fallos, "Con CA muy baja solo la pifia (1 natural) puede fallar");
        }
        if (objetivo.getClaseArmadura() >= 50) {
            assertEquals(0, impactos, "Con CA muy alta solo el crítico (20 natural) puede impactar");
            assertTrue(fallos > 0, "Con CA muy alta debe haber fallos");
        }
    }
}
