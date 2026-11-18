/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 8. BARDO (Req Multiclase D&D 5e: Carisma >= 13)
public class Bardo extends ClasePersonaje {
    private final String instrumentoMusical;

    public Bardo(int nivel, String instrumentoMusical) throws ReglaDndException {
        super("Bardo", nivel);
        if (instrumentoMusical == null || instrumentoMusical.trim().isEmpty()) {
            throw new ReglaDndException("Debe indicar el instrumento musical del Bardo.");
        }
        this.instrumentoMusical = instrumentoMusical.trim();
    }

    public String getInstrumentoMusical() { return instrumentoMusical; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) { return av.getCarisma() >= 13; }

    @Override
    public String getMensajePrerrequisito() { return "Bardo requiere Carisma 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        String frase = "El Bardo " + atacante.getNombre() + " toca su " + instrumentoMusical + " para otorgar Inspiración Bárdica a un aliado.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModCarisma(), 1, 6, "Burla Dañina", frase);
    }
}

