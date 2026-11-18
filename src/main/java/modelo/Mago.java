/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 1. MAGO (Req Multiclase D&D 5e: Inteligencia >= 13)
public class Mago extends ClasePersonaje {
    private int espaciosConjuro;

    public Mago(int nivel, int espaciosConjuro) throws ReglaDndException {
        super("Mago", nivel);
        if (espaciosConjuro < 0) throw new ReglaDndException("Los espacios de conjuro no pueden ser negativos.");
        this.espaciosConjuro = espaciosConjuro;
    }

    public int getEspaciosConjuro() { return espaciosConjuro; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) { return av.getInteligencia() >= 13; }

    @Override
    public String getMensajePrerrequisito() { return "Mago requiere Inteligencia 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        if (espaciosConjuro <= 0) {
            return "El Mago " + atacante.getNombre() + " no tiene espacios de conjuro y lanza un truco de Descarga de Fuego.";
        }
        espaciosConjuro--;
        String frase = "El Mago " + atacante.getNombre() + " gasta un espacio de conjuro (restan " + espaciosConjuro + ") para lanzar una poderosa Bola de Fuego.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModInteligencia(), 3, 6, "Bola de Fuego", frase);
    }
}

