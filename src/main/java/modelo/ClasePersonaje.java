/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author oriana
 */
public abstract class ClasePersonaje implements IAccionCombate {
    protected final String nombreClase;
    protected int nivel;

    public ClasePersonaje(String nombreClase, int nivel) throws ReglaDndException {
        if (nivel < 1 || nivel > 20) {
            throw new ReglaDndException("El nivel de la clase " + nombreClase + " debe estar entre 1 y 20.");
        }
        this.nombreClase = nombreClase;
        this.nivel = nivel;
    }

    public String getNombreClase() { return nombreClase; }
    public int getNivel() { return nivel; }

    public abstract boolean cumplePrerrequisitoMulticlase(Aventurero av);
    public abstract String getMensajePrerrequisito();
    public abstract String getAtributoEspecial();

    protected String resolverAtaqueD20(Aventurero atacante, Aventurero objetivo, int modAtributo,
                                       int numDados, int carasDado, String nombreHabilidad, String fraseBase) {
        int natural = DadoD20.lanzar();
        int bono = modAtributo + atacante.getBonoCompetencia();
        int totalAtaque = natural + bono;

        StringBuilder sb = new StringBuilder();
        sb.append(fraseBase).append("\n   -> Tirada d20: [").append(natural).append("] + ").append(bono)
          .append(" = ").append(totalAtaque).append(" vs CA ").append(objetivo.getClaseArmadura());

        if (DadoD20.esPifia(natural)) {
            sb.append(" -> ¡PIFIA NATURAL (1)! El ataque falla estrepitosamente.");
        } else if (DadoD20.esCritico(natural)) {
            int danioCritico = DadoD20.lanzarDanio(numDados * 2, carasDado, modAtributo);
            objetivo.recibirDanio(danioCritico);
            sb.append(" -> ¡GOLPE CRÍTICO (20)! Duplica dados de daño (").append(nombreHabilidad)
              .append("): -").append(danioCritico).append(" PG a ").append(objetivo.getNombre())
              .append(" (PG restantes: ").append(objetivo.getPuntosVidaActual()).append(").");
        } else if (totalAtaque >= objetivo.getClaseArmadura()) {
            int danio = DadoD20.lanzarDanio(numDados, carasDado, modAtributo);
            objetivo.recibirDanio(danio);
            sb.append(" -> ¡IMPACTO! (").append(nombreHabilidad).append("): -").append(danio)
              .append(" PG a ").append(objetivo.getNombre())
              .append(" (PG restantes: ").append(objetivo.getPuntosVidaActual()).append(").");
        } else {
            sb.append(" -> FALLO. La defensa de ").append(objetivo.getNombre()).append(" bloquea el golpe.");
        }
        return sb.toString();
    }
}
