/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 1. GUERRERO (Req Multiclase D&D 5e: Fuerza >= 13 o Destreza >= 13)
public class Guerrero extends ClasePersonaje {
    private final boolean armaduraPesada;

    public Guerrero(int nivel, boolean armaduraPesada) throws ReglaDndException {
        super("Guerrero", nivel);
        this.armaduraPesada = armaduraPesada;
    }

    public boolean isArmaduraPesada() { return armaduraPesada; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) {
        return av.getFuerza() >= 13 || av.getDestreza() >= 13;
    }

    @Override
    public String getMensajePrerrequisito() { return "Guerrero requiere Fuerza 13 o Destreza 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        int mod = Math.max(atacante.getModFuerza(), atacante.getModDestreza());
        String frase = "El Guerrero " + atacante.getNombre() + " usa Oleada de Acción para realizar múltiples ataques físicos continuos"
                + (armaduraPesada ? " (protegido con Armadura Pesada)." : " (estilo ágil).");
        return resolverAtaqueD20(atacante, objetivo, mod, 2, 8, "Oleada de Acción", frase);
    }
    
    @Override
    public String getAtributoEspecial() {
        return "armaduraPesada: " + (armaduraPesada ? "sí" : "no");
    }
}
