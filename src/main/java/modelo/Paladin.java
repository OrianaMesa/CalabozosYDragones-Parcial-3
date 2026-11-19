/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 5. PALADÍN (Req Multiclase D&D 5e: Fuerza >= 13 y Carisma >= 13)
public class Paladin extends ClasePersonaje {
    private int puntosFuerza;

    public Paladin(int nivel, int puntosFuerza) throws ReglaDndException {
        super("Paladín", nivel);
        if (puntosFuerza < 0) throw new ReglaDndException("Los puntos de fuerza/imposición no pueden ser negativos.");
        this.puntosFuerza = puntosFuerza;
    }

    public int getPuntosFuerza() { return puntosFuerza; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) {
        return av.getFuerza() >= 13 && av.getCarisma() >= 13;
    }

    @Override
    public String getMensajePrerrequisito() { return "Paladín requiere Fuerza 13 y Carisma 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        String frase = "El Paladín " + atacante.getNombre() + " (Poder: " + puntosFuerza + ") consume magia para imbuir su arma con un Castigo Divino de daño radiante.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModFuerza(), 2, 8, "Castigo Divino", frase);
    }
    
    @Override
    public String getAtributoEspecial() {
        return "puntosFuerza: " + puntosFuerza;
    }
}
