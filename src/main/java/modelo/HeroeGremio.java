/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */
public class HeroeGremio extends Aventurero {
    private String tituloGremio;

    public HeroeGremio(String nombre, int puntosVida, int claseArmadura,
                       int fuerza, int destreza, int constitucion,
                       int inteligencia, int sabiduria, int carisma,
                       String tituloGremio) throws ReglaDndException {
        super(nombre, puntosVida, claseArmadura, fuerza, destreza, constitucion, inteligencia, sabiduria, carisma);
        this.tituloGremio = (tituloGremio == null || tituloGremio.trim().isEmpty()) ? "Aventurero de Costa de la Espada" : tituloGremio.trim();
    }

    public String getTituloGremio() {
        return tituloGremio;
    }

    public void setTituloGremio(String tituloGremio) {
        this.tituloGremio = tituloGremio;
    }

    @Override
    public String ejecutarTurnoCompleto(Aventurero objetivo) {
        StringBuilder sb = new StringBuilder();
        sb.append(">>> Turno de ").append(nombre).append(" [").append(getResumenClases())
          .append(" | Nv. Total ").append(getNivelTotal()).append(" | PG ").append(puntosVidaActual).append("/").append(puntosVidaMax).append("]:\n");
        for (ClasePersonaje cp : clases) {
            if (!objetivo.estaVivo()) break;
            sb.append("  * ").append(cp.ejecutarAccionTactica(this, objetivo)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Nv.Total: %d | Clases: [%s] | PG: %d/%d | CA: %d",
                nombre, tituloGremio, getNivelTotal(), getResumenClases(), puntosVidaActual, puntosVidaMax, claseArmadura);
    }
}

