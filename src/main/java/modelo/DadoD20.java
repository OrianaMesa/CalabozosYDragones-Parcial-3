/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.Random;
/**
 *
 * @author oriana
 */
public class DadoD20 {
    
    public static final int MAX_CARAS = 20;
    private static final Random RANDOM = new Random();

    private DadoD20() {}

    // Sobrecarga 1: Tirada pura de d20
    public static int lanzar() {
        return RANDOM.nextInt(MAX_CARAS) + 1;
    }

    // Sobrecarga 2: Tirada de d20 + modificador
    public static int lanzar(int modificador) {
        return lanzar() + modificador;
    }

    
    public static int lanzarDanio(int cantidadDados, int carasDado, int modificador) {
        int total = modificador;
        for (int i = 0; i < cantidadDados; i++) {
            total += RANDOM.nextInt(carasDado) + 1;
        }
        return Math.max(1, total);
    }

    public static boolean esCritico(int tiradaNatural) {
        return tiradaNatural == 20;
    }

    public static boolean esPifia(int tiradaNatural) {
        return tiradaNatural == 1;
    }
}

