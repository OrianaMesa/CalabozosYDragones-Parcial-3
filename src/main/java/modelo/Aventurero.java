/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author oriana
 */

public abstract class Aventurero {
    public static final int MAX_CLASES = 4;
    public static final int MAX_NIVEL_TOTAL = 20;

    protected final String nombre;
    protected final int puntosVidaMax;
    protected int puntosVidaActual;
    protected final int claseArmadura;
    protected final int fuerza;
    protected final int destreza;
    protected final int constitucion;
    protected final int inteligencia;
    protected final int sabiduria;
    protected final int carisma;
    protected final List<ClasePersonaje> clases;

    public Aventurero(String nombre, int puntosVida, int claseArmadura,
                      int fuerza, int destreza, int constitucion,
                      int inteligencia, int sabiduria, int carisma) throws ReglaDndException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaDndException("El nombre del aventurero no puede estar vacío.");
        }
        if (puntosVida <= 0) {
            throw new ReglaDndException("Los puntos de vida deben ser mayores a 0.");
        }
        this.nombre = nombre.trim();
        this.puntosVidaMax = puntosVida;
        this.puntosVidaActual = puntosVida;
        this.claseArmadura = claseArmadura;
        this.fuerza = fuerza;
        this.destreza = destreza;
        this.constitucion = constitucion;
        this.inteligencia = inteligencia;
        this.sabiduria = sabiduria;
        this.carisma = carisma;
        this.clases = new ArrayList<>();
    }

    public void agregarClase(ClasePersonaje nuevaClase) throws ReglaDndException {
        if (clases.size() >= MAX_CLASES) {
            throw new ReglaDndException("Un aventurero no puede tener más de " + MAX_CLASES + " clases.");
        }
        for (ClasePersonaje c : clases) {
            if (c.getNombreClase().equalsIgnoreCase(nuevaClase.getNombreClase())) {
                throw new ReglaDndException("La clase " + nuevaClase.getNombreClase() + " ya está asignada a " + nombre + " (prohibido repetir clase).");
            }
        }
        if (getNivelTotal() + nuevaClase.getNivel() > MAX_NIVEL_TOTAL) {
            throw new ReglaDndException("El nivel total (" + (getNivelTotal() + nuevaClase.getNivel()) + ") excede el máximo permitido de " + MAX_NIVEL_TOTAL + ".");
        }
        if (!clases.isEmpty()) {
            if (!clases.get(0).cumplePrerrequisitoMulticlase(this)) {
                throw new ReglaDndException("No cumple prerrequisito de su clase inicial para multiclase: " + clases.get(0).getMensajePrerrequisito());
            }
            if (!nuevaClase.cumplePrerrequisitoMulticlase(this)) {
                throw new ReglaDndException("No cumple prerrequisito para multiclase: " + nuevaClase.getMensajePrerrequisito());
            }
        }
        clases.add(nuevaClase);
    }

    public int getNivelTotal() {
        return clases.stream().mapToInt(ClasePersonaje::getNivel).sum();
    }

    public int getBonoCompetencia() {
        int n = Math.max(1, getNivelTotal());
        return 2 + (n - 1) / 4;
    }

    public int getModFuerza() { return Math.floorDiv(fuerza - 10, 2); }
    public int getModDestreza() { return Math.floorDiv(destreza - 10, 2); }
    public int getModConstitucion() { return Math.floorDiv(constitucion - 10, 2); }
    public int getModInteligencia() { return Math.floorDiv(inteligencia - 10, 2); }
    public int getModSabiduria() { return Math.floorDiv(sabiduria - 10, 2); }
    public int getModCarisma() { return Math.floorDiv(carisma - 10, 2); }

    public int tirarIniciativa() {
        return DadoD20.lanzar(getModDestreza());
    }

    public void recibirDanio(int danio) {
        this.puntosVidaActual = Math.max(0, this.puntosVidaActual - danio);
    }

    public void curar(int cantidad) {
        this.puntosVidaActual = Math.min(this.puntosVidaMax, this.puntosVidaActual + cantidad);
    }

    public void restaurarVida() {
        this.puntosVidaActual = this.puntosVidaMax;
    }

    public boolean estaVivo() {
        return this.puntosVidaActual > 0;
    }

    public String getResumenClases() {
        return clases.stream()
                .map(c -> c.getNombreClase() + " Nv." + c.getNivel())
                .collect(Collectors.joining(" / "));
    }

    public String getNombre() { return nombre; }
    public int getPuntosVidaMax() { return puntosVidaMax; }
    public int getPuntosVidaActual() { return puntosVidaActual; }
    public int getClaseArmadura() { return claseArmadura; }
    public int getFuerza() { return fuerza; }
    public int getDestreza() { return destreza; }
    public int getInteligencia() { return inteligencia; }
    public int getSabiduria() { return sabiduria; }
    public int getCarisma() { return carisma; }
    public List<ClasePersonaje> getClases() { return Collections.unmodifiableList(clases); }
    
    /** Título por defecto; HeroeGremio lo sobrescribe con el título del gremio. */
    public String getTitulo() {
        return "Aventurero del Gremio";
    }

    /** Atributo especial de cada clase del aventurero, unidos en un solo texto. */
    public String getAtributoEspecial() {
        return clases.stream()
                .map(c -> c.getNombreClase() + " (" + c.getAtributoEspecial() + ")")
                .collect(Collectors.joining("; "));
    }

    public abstract String ejecutarTurnoCompleto(Aventurero objetivo);
}
