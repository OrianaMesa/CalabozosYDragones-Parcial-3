/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 4. MONJE (Req Multiclase D&D 5e: Destreza >= 13 y Sabiduría >= 13)
public class Monje extends ClasePersonaje {
    private int puntosKi;

    public Monje(int nivel, int puntosKi) throws ReglaDndException {
        super("Monje", nivel);
        if (puntosKi < 0) throw new ReglaDndException("Los puntos de Ki no pueden ser negativos.");
        this.puntosKi = puntosKi;
    }

    public int getPuntosKi() { return puntosKi; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) {
        return av.getDestreza() >= 13 && av.getSabiduria() >= 13;
    }

    @Override
    public String getMensajePrerrequisito() { return "Monje requiere Destreza 13 y Sabiduría 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        if (puntosKi > 0) puntosKi--;
        String frase = "El Monje " + atacante.getNombre() + " gasta puntos de Ki (restan " + puntosKi + ") para ejecutar una Ráfaga de Golpes y un Ataque Aturdidor.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModDestreza(), 2, 6, "Ráfaga de Golpes", frase);
    }
}

