/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.calabozosydragones;
import controlador.GremioControlador;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import vista.VistaGremio;
/**
 *
 * @author oriana
 */
public class CalabozosYDragones {

    public static void main(String[] args) {
        GremioControlador controlador = new GremioControlador();

        EventQueue.invokeLater(() -> {
            VistaGremio vista = new VistaGremio(controlador);
            vista.setLocationRelativeTo(null);
            vista.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    imprimirReportes(controlador);
                }
            });
            vista.setVisible(true);
        });
    }

    private static void imprimirReportes(GremioControlador controlador) {
        System.out.println(controlador.generarReporteGremio());       // Reporte 1: niveles totales
        System.out.println(controlador.generarReporteCombinaciones()); // Reporte 2: combinaciones multiclase
    }
}
