/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.ArrayList;
import modelo.Aventurero;
import modelo.ReglaDndException;
import persistencia.GestorPersistencia;
/**
 *
 * @author oriana
 */
public class GremioControlador {
    public static final int MAX_AVENTUREROS = 4;
    private final ArrayList<Aventurero> gremio;

    public GremioControlador() {
        this.gremio = new ArrayList<Aventurero>();
    }

    public void registrarAventurero(Aventurero av) throws ReglaDndException {
        if (gremio.size() >= MAX_AVENTUREROS) {
            throw new ReglaDndException("El gremio ya alcanzó su límite máximo de " + MAX_AVENTUREROS + " aventureros.");
        }
        if (av.getClases().isEmpty()) {
            throw new ReglaDndException("El aventurero debe tener al menos 1 clase asignada.");
        }
        for (int i = 0; i < gremio.size(); i++) {
            Aventurero existente = gremio.get(i);
            if (existente.getNombre().equalsIgnoreCase(av.getNombre())) {
                throw new ReglaDndException("Ya existe un aventurero registrado con el nombre: " + av.getNombre());
            }
        }
        gremio.add(av);
        GestorPersistencia.guardarEvento("REGISTRO DE AVENTURERO:\n" + av.toString());
    }

    public ArrayList<Aventurero> getGremio() {
        return new ArrayList<Aventurero>(gremio);
    }

    public String simularCombate() throws ReglaDndException {
        if (gremio.size() < 2) {
            throw new ReglaDndException("Se necesitan al menos 2 aventureros en el gremio para iniciar el combate.");
        }

        // Restaurar PG antes de iniciar el combate
        for (int i = 0; i < gremio.size(); i++) {
            gremio.get(i).restaurarVida();
        }

        StringBuilder log = new StringBuilder();
        log.append("=== SIMULACIÓN DE COMBATE TÁCTICO D&D 5E ===\n");
        log.append("1. TIRADA DE INICIATIVA (1d20 + Mod. Destreza):\n");

        // Guardamos iniciativas en un ArrayList paralelo del mismo tamaño
        ArrayList<Integer> iniciativas = new ArrayList<Integer>();
        ArrayList<Aventurero> ordenTurnos = new ArrayList<Aventurero>();

        for (int i = 0; i < gremio.size(); i++) {
            Aventurero av = gremio.get(i);
            int ini = av.tirarIniciativa();
            ordenTurnos.add(av);
            iniciativas.add(ini);
            log.append("   - ").append(av.getNombre())
               .append(": ").append(ini)
               .append(" (Mod. DES ").append(av.getModDestreza()).append(")\n");
        }

        // Ordenar de mayor a menor iniciativa con bucle for anidado básico
        for (int i = 0; i < ordenTurnos.size() - 1; i++) {
            for (int j = i + 1; j < ordenTurnos.size(); j++) {
                if (iniciativas.get(j) > iniciativas.get(i)) {
                    int tempIni = iniciativas.get(i);
                    iniciativas.set(i, iniciativas.get(j));
                    iniciativas.set(j, tempIni);

                    Aventurero tempHeroe = ordenTurnos.get(i);
                    ordenTurnos.set(i, ordenTurnos.get(j));
                    ordenTurnos.set(j, tempHeroe);
                }
            }
        }

        log.append("\n2. ORDEN DE TURNOS ESTABLECIDO:\n   ");
        for (int i = 0; i < ordenTurnos.size(); i++) {
            log.append(i + 1).append(". ").append(ordenTurnos.get(i).getNombre())
               .append(" (").append(iniciativas.get(i)).append(")  ");
        }
        log.append("\n\n3. DESARROLLO DEL COMBATE:\n");

        int ronda = 1;
        while (contarVivos() > 1 && ronda <= 5) {
            log.append("--- RONDA ").append(ronda).append(" ---\n");
            for (int i = 0; i < ordenTurnos.size(); i++) {
                Aventurero atacante = ordenTurnos.get(i);
                if (!atacante.estaVivo()) {
                    continue;
                }
                Aventurero objetivo = buscarObjetivo(atacante, ordenTurnos);
                if (objetivo == null) {
                    break;
                }
                log.append(atacante.ejecutarTurnoCompleto(objetivo));
            }
            ronda++;
        }

        Aventurero ganador = ordenTurnos.get(0);
        for (int i = 0; i < ordenTurnos.size(); i++) {
            Aventurero actual = ordenTurnos.get(i);
            if (actual.estaVivo() && actual.getPuntosVidaActual() >= ganador.getPuntosVidaActual()) {
                ganador = actual;
            }
        }

        log.append("\n=== RESULTADO FINAL ===\n");
        log.append("Vencedor del encuentro: ").append(ganador.getNombre())
           .append(" con ").append(ganador.getPuntosVidaActual())
           .append("/").append(ganador.getPuntosVidaMax()).append(" PG.\n");

        String resultado = log.toString();
        GestorPersistencia.guardarEvento(resultado);
        return resultado;
    }

    private int contarVivos() {
        int vivos = 0;
        for (int i = 0; i < gremio.size(); i++) {
            if (gremio.get(i).estaVivo()) {
                vivos++;
            }
        }
        return vivos;
    }

    private Aventurero buscarObjetivo(Aventurero atacante, ArrayList<Aventurero> orden) {
        for (int i = 0; i < orden.size(); i++) {
            Aventurero candidato = orden.get(i);
            if (candidato != atacante && candidato.estaVivo()) {
                return candidato;
            }
        }
        return null;
    }

    public String generarReporteGremio() {
        if (gremio.isEmpty()) {
            return "No hay aventureros registrados en el gremio.";
        }
        StringBuilder sb = new StringBuilder("=== REPORTE DE AVENTUREROS Y NIVELES TOTALES ===\n");
        for (int i = 0; i < gremio.size(); i++) {
            Aventurero a = gremio.get(i);
            sb.append(i + 1).append(". ").append(a.getNombre())
              .append(" | Nivel Total: ").append(a.getNivelTotal()).append("/20")
              .append(" | Bono Competencia: +").append(a.getBonoCompetencia())
              .append(" | PG: ").append(a.getPuntosVidaMax())
              .append(" | CA: ").append(a.getClaseArmadura()).append("\n");
        }
        return sb.toString();
    }

    public String generarReporteCombinaciones() {
        if (gremio.isEmpty()) {
            return "No hay combinaciones registradas.";
        }
        StringBuilder sb = new StringBuilder("=== REPORTE DE COMBINACIONES MULTICLASE ===\n");
        for (int i = 0; i < gremio.size(); i++) {
            Aventurero a = gremio.get(i);
            String tipo = a.getClases().size() > 1
                    ? "MULTICLASE (" + a.getClases().size() + " clases)"
                    : "CLASE ÚNICA";
            sb.append("- ").append(a.getNombre()).append(" [").append(tipo).append("]: ")
              .append(a.getResumenClases()).append(" = Nivel Total ").append(a.getNivelTotal()).append("\n");
        }
        return sb.toString();
    }
}