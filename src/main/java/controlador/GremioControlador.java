/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import java.util.ArrayList;
import modelo.Aventurero;
import modelo.Bardo;
import modelo.ClasePersonaje;
import modelo.Clerigo;
import modelo.Explorador;
import modelo.Guerrero;
import modelo.HeroeGremio;
import modelo.Mago;
import modelo.Monje;
import modelo.Paladin;
import modelo.Picaro;
import modelo.ReglaDndException;
import persistencia.GestorPersistencia;
/**
 *
 * @author oriana
 */
public class GremioControlador {
    public static final int MAX_AVENTUREROS = 4;
    public static final String TITULO_POR_DEFECTO = "Aventurero del Gremio";

    private final ArrayList<Aventurero> gremio;
    // Borrador: clases que el usuario va agregando antes de registrar al aventurero.
    private final ArrayList<ClasePersonaje> clasesTemporales;

    public GremioControlador() {
        this.gremio = new ArrayList<Aventurero>();
        this.clasesTemporales = new ArrayList<ClasePersonaje>();
    }

    // ------------------------------------------------------------------
    // BORRADOR DE CLASES (formulario de registro)
    // ------------------------------------------------------------------

    /**
     * Valida y guarda una clase en el borrador. Aplica en este mismo momento TODAS las
     * reglas (máx. 4 clases, sin repetir, nivel total <= 20 y prerrequisitos de multiclase
     * con los atributos que el usuario tiene en el formulario), de modo que el error se
     * informa al agregar la clase y no hasta presionar "Registrar".
     * Si algo falla lanza ReglaDndException y el borrador queda intacto.
     */
    public void agregarClaseTemporal(String nombreClase, int nivel, String atributoEspecial,
                                     int fuerza, int destreza, int constitucion,
                                     int inteligencia, int sabiduria, int carisma) throws ReglaDndException {
        if (nombreClase == null || nombreClase.trim().isEmpty()) {
            throw new ReglaDndException("Debes seleccionar una clase.");
        }
        if (atributoEspecial == null || atributoEspecial.trim().isEmpty()) {
            throw new ReglaDndException("Debes indicar el atributo especial de la clase.");
        }
        if (clasesTemporales.size() >= Aventurero.MAX_CLASES) {
            throw new ReglaDndException("Un aventurero solo puede tener hasta " + Aventurero.MAX_CLASES + " clases.");
        }

        ClasePersonaje nueva = crearClase(nombreClase, nivel, atributoEspecial);

        for (int i = 0; i < clasesTemporales.size(); i++) {
            if (clasesTemporales.get(i).getNombreClase().equalsIgnoreCase(nueva.getNombreClase())) {
                throw new ReglaDndException("La clase " + nueva.getNombreClase()
                        + " ya fue agregada a este aventurero (prohibido repetir clase).");
            }
        }

        // Aventurero "de prueba" con los atributos actuales: reutiliza las reglas del modelo
        // (Aventurero.agregarClase) sin duplicarlas aquí.
        Aventurero prueba = new HeroeGremio("Borrador", 1, 10,
                fuerza, destreza, constitucion, inteligencia, sabiduria, carisma, TITULO_POR_DEFECTO);
        for (int i = 0; i < clasesTemporales.size(); i++) {
            prueba.agregarClase(clasesTemporales.get(i));
        }
        
        // Requisito propio de la clase, también cuando es la primera (sin multiclase).
        if (!nueva.cumplePrerrequisitoMulticlase(prueba)) {
            throw new ReglaDndException("No cumple el requisito de " + nueva.getNombreClase()
                    + ": " + nueva.getMensajePrerrequisito());
        }
        prueba.agregarClase(nueva);

        clasesTemporales.add(nueva);
    }

    public void limpiarClasesTemporales() {
        clasesTemporales.clear();
    }

    public boolean hayClasesTemporales() {
        return !clasesTemporales.isEmpty();
    }

    /** Texto del resumen del borrador: "Clases (2/4 | Nv.Total 7/20): Guerrero Nv.4 + Clérigo Nv.3". */
    public String getResumenClasesTemporales() {
        int suma = 0;
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < clasesTemporales.size(); i++) {
            ClasePersonaje c = clasesTemporales.get(i);
            if (i > 0) {
                texto.append(" + ");
            }
            texto.append(c.getNombreClase()).append(" Nv.").append(c.getNivel());
            suma += c.getNivel();
        }
        String encabezado = "Clases (" + clasesTemporales.size() + "/" + Aventurero.MAX_CLASES
                + " | Nv.Total " + suma + "/" + Aventurero.MAX_NIVEL_TOTAL + "): ";
        return encabezado + (clasesTemporales.isEmpty() ? "Ninguna clase añadida" : texto.toString());
    }

    /** Etiqueta que la vista muestra junto al campo "atributo especial" según la clase elegida. */
    public String getEtiquetaAtributoEspecial(String clase) {
        if (clase == null) {
            return "Atributo Especial (texto):";
        }
        String c = clase.trim().toLowerCase();
        switch (c) {
            case "mago":
                return "Espacios de Conjuro (entero):";
            case "guerrero":
                return "Armadura Pesada (texto: si/no):";
            case "clérigo":
            case "clerigo":
                return "Dios Venerado (texto):";
            case "monje":
                return "Puntos de Ki (entero):";
            case "paladín":
            case "paladin":
                return "Puntos de Fuerza (entero):";
            case "explorador":
                return "Enemigo Predilecto (texto):";
            case "pícaro":
            case "picaro":
                return "Herramientas de Ladrón (texto: si/no):";
            case "bardo":
                return "Especialidad Musical (texto):";
            default:
                return "Atributo Especial (texto):";
        }
    }

    // ------------------------------------------------------------------
    // REGISTRO
    // ------------------------------------------------------------------

    /**
     * Arma el aventurero con los datos del formulario y las clases del borrador, lo registra
     * en el gremio y limpia el borrador. Si falla, el borrador se conserva para corregir.
     */
    public Aventurero registrarDesdeFormulario(String nombre, String titulo, int vidaMax, int claseArmadura,
                                               int fuerza, int destreza, int constitucion,
                                               int inteligencia, int sabiduria, int carisma) throws ReglaDndException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaDndException("Debes ingresar el nombre del aventurero.");
        }
        if (clasesTemporales.isEmpty()) {
            throw new ReglaDndException("Añade al menos una clase antes de registrar al aventurero.");
        }
        String tituloFinal = (titulo == null || titulo.trim().isEmpty()) ? TITULO_POR_DEFECTO : titulo.trim();

        Aventurero aventurero = new HeroeGremio(nombre, vidaMax, claseArmadura,
                fuerza, destreza, constitucion, inteligencia, sabiduria, carisma, tituloFinal);
        for (int i = 0; i < clasesTemporales.size(); i++) {
            aventurero.agregarClase(clasesTemporales.get(i));
        }

        registrarAventurero(aventurero);
        clasesTemporales.clear();
        return aventurero;
    }

    /** Fábrica: crea la ClasePersonaje según el nombre del combo y convierte el atributo especial. */
    private ClasePersonaje crearClase(String clase, int nivel, String atributo) throws ReglaDndException {
        String c = clase.trim().toLowerCase();
        switch (c) {
            case "guerrero":
                return new Guerrero(nivel, leerSiNo(atributo, "Armadura pesada"));
            case "mago":
                return new Mago(nivel, leerEntero(atributo, "Espacios de conjuro"));
            case "clérigo":
            case "clerigo":
                return new Clerigo(nivel, atributo);
            case "monje":
                return new Monje(nivel, leerEntero(atributo, "Puntos de Ki"));
            case "paladín":
            case "paladin":
                return new Paladin(nivel, leerEntero(atributo, "Puntos de fuerza"));
            case "explorador":
                return new Explorador(nivel, atributo);
            case "pícaro":
            case "picaro":
                return new Picaro(nivel, leerSiNo(atributo, "Herramientas de ladrón"));
            case "bardo":
                return new Bardo(nivel, atributo);
            default:
                throw new ReglaDndException("Clase no reconocida: " + clase);
        }
    }

    private boolean leerSiNo(String texto, String campo) throws ReglaDndException {
        String t = texto.trim().toLowerCase();
        if (t.equals("si") || t.equals("sí") || t.equals("s") || t.equals("true")) {
            return true;
        }
        if (t.equals("no") || t.equals("n") || t.equals("false")) {
            return false;
        }
        throw new ReglaDndException("El campo '" + campo + "' debe ser si o no.");
    }

    private int leerEntero(String texto, String campo) throws ReglaDndException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new ReglaDndException("El campo '" + campo + "' debe ser un número entero.");
        }
    }

    // ------------------------------------------------------------------
    // GREMIO / HISTORIAL
    // ------------------------------------------------------------------

    /** La vista nunca toca la persistencia: pasa por el controlador. */
    public String leerHistorial() {
        return GestorPersistencia.leerHistorial();
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