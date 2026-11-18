/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */

// 3. CLÉRIGO (Req Multiclase D&D 5e: Sabiduría >= 13)
public class Clerigo extends ClasePersonaje {
    private final String diosVenerado;

    public Clerigo(int nivel, String diosVenerado) throws ReglaDndException {
        super("Clérigo", nivel);
        if (diosVenerado == null || diosVenerado.trim().isEmpty()) {
            throw new ReglaDndException("Debe indicar el dios venerado del Clérigo.");
        }
        this.diosVenerado = diosVenerado.trim();
    }

    public String getDiosVenerado() { return diosVenerado; }

    @Override
    public boolean cumplePrerrequisitoMulticlase(Aventurero av) { return av.getSabiduria() >= 13; }

    @Override
    public String getMensajePrerrequisito() { return "Clérigo requiere Sabiduría 13."; }

    @Override
    public String ejecutarAccionTactica(Aventurero atacante, Aventurero objetivo) {
        int curacion = DadoD20.lanzarDanio(1, 8, atacante.getModSabiduria());
        atacante.curar(curacion);
        String frase = "El Clérigo " + atacante.getNombre() + " invoca a " + diosVenerado
                + " y usa Canalizar Divinidad para curar a sus aliados (+" + curacion + " PG) y expulsar muertos vivientes.";
        return resolverAtaqueD20(atacante, objetivo, atacante.getModSabiduria(), 1, 8, "Llama Sagrada", frase);
    }
}

