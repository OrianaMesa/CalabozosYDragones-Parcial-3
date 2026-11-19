/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author oriana
 */
public final class GestorPersistencia {
    public static final String RUTA_ARCHIVO = "registro_gremio.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GestorPersistencia() {}

    public static void guardarEvento(String evento) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write("[" + LocalDateTime.now().format(FMT) + "] " + evento);
            bw.newLine();
            bw.write("----------------------------------------------------------------------");
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en " + RUTA_ARCHIVO + ": " + e.getMessage());
        }
    }

    public static String leerHistorial() {
        try {
            Path path = Paths.get(RUTA_ARCHIVO);
            return Files.exists(path) ? new String(Files.readAllBytes(path), "UTF-8") : "Aún no existen registros en el gremio.";
        } catch (IOException e) {
            return "Error al leer el historial: " + e.getMessage();
        }
    }
}