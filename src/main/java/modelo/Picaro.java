/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 7. PÍCARO (Req Multiclase D&D 5e: Destreza >= 13)
public class Picaro extends ClasePersonaje {
    private final boolean herramientasLadron;

    public Picaro(int nivel, boolean herramientasLadron) throws ReglaDndException {
        super("Pícaro", nivel);
        this.herramientasLadron = herramientasLadron;
    }

    public boolean isHerramientasLadron() { return herramientasLadron; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) { return av.getDestreza() >= 13; }

    @Override
    public String getMensajePrerrequisito() { return "Pícaro requiere Destreza 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        int dadosFurtivos = Math.max(1, (nivel + 1) / 2);
        String frase = "El Pícaro " + atacante.getNombre() + " aprovecha la ventaja táctica para asestar un Ataque Furtivo masivo y se reposiciona"
                + (herramientasLadron ? " [Herramientas de ladrón listas]." : ".");
        return resolverAtaqueD20(atacante, objetivo, atacante.getModDestreza(), dadosFurtivos, 6, "Ataque Furtivo", frase);
    }
}