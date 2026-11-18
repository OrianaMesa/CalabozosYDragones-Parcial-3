/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 6. EXPLORADOR (Req Multiclase D&D 5e: Destreza >= 13 y Sabiduría >= 13)
public class Explorador extends ClasePersonaje {
    private final String terrenoFavorecido;

    public Explorador(int nivel, String terrenoFavorecido) throws ReglaDndException {
        super("Explorador", nivel);
        if (terrenoFavorecido == null || terrenoFavorecido.trim().isEmpty()) {
            throw new ReglaDndException("Debe indicar el terreno favorecido del Explorador.");
        }
        this.terrenoFavorecido = terrenoFavorecido.trim();
    }

    public String getTerrenoFavorecido() { return terrenoFavorecido; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) {
        return av.getDestreza() >= 13 && av.getSabiduria() >= 13;
    }

    @Override
    public String getMensajePrerrequisito() { return "Explorador requiere Destreza 13 y Sabiduría 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        String frase = "El Explorador " + atacante.getNombre() + " (experto en " + terrenoFavorecido + ") aplica Marca del Cazador sobre el objetivo para maximizar su daño sostenido.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModDestreza(), 1, 8, "Marca del Cazador (+1d6)", frase);
    }
}
