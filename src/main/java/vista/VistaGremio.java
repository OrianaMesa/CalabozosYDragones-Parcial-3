/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.GremioControlador;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Aventurero;
import modelo.Bardo;
import modelo.Clerigo;
import modelo.ClasePersonaje;
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
public class VistaGremio extends javax.swing.JFrame {
    
    private final GremioControlador controlador;
    private final ArrayList<String> clasesTemporales;
    private final ArrayList<Integer> nivelesTemporales;
    private final ArrayList<String> atributosTemporales;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaGremio.class.getName());

    /**
     * Creates new form VistaGremio
     */
    public VistaGremio() {
        initComponents();
        this.controlador = new GremioControlador();
        this.clasesTemporales = new ArrayList<String>();
        this.nivelesTemporales = new ArrayList<Integer>();
        this.atributosTemporales = new ArrayList<String>();

        configurarTabla();
        actualizarEtiquetaAtributo();
        actualizarResumenClases();
        actualizarTablaGremio();
    }
    
    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Nombre", "Título / Trasfondo", "Clases", "Nv. Total", "PG", "CA", "Atributo Especial"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGremio.setModel(modelo);
    }

    private void actualizarEtiquetaAtributo() {
        Object seleccion = cmbClase.getSelectedItem();
        if (seleccion == null) {
            return;
        }
        String clase = seleccion.toString();

        if (clase.equalsIgnoreCase("Mago")) {
            lblAtributoEspecial.setText("Espacios de Conjuro (entero):");
        } else if (clase.equalsIgnoreCase("Guerrero")) {
            lblAtributoEspecial.setText("Armadura Pesada (texto: si/no):");
        } else if (clase.equalsIgnoreCase("Clérigo") || clase.equalsIgnoreCase("Clerigo")) {
            lblAtributoEspecial.setText("Dios Venerado (texto):");
        } else if (clase.equalsIgnoreCase("Monje")) {
            lblAtributoEspecial.setText("Puntos de Ki (entero):");
        } else if (clase.equalsIgnoreCase("Paladín") || clase.equalsIgnoreCase("Paladin")) {
            lblAtributoEspecial.setText("Puntos de Fuerza (entero):");
        } else if (clase.equalsIgnoreCase("Explorador")) {
            lblAtributoEspecial.setText("Enemigo Predilecto (texto):");
        } else if (clase.equalsIgnoreCase("Pícaro") || clase.equalsIgnoreCase("Picaro")) {
            lblAtributoEspecial.setText("Herramientas de Ladrón (texto: si/no):");
        } else if (clase.equalsIgnoreCase("Bardo")) {
            lblAtributoEspecial.setText("Especialidad Musical (texto):");
        } else {
            lblAtributoEspecial.setText("Atributo Especial (texto):");
        }
    }

    private void actualizarResumenClases() {
        int sumaNiveles = 0;
        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < clasesTemporales.size(); i++) {
            if (i > 0) {
                texto.append(" + ");
            }
            texto.append(clasesTemporales.get(i))
                 .append(" Nv.")
                 .append(nivelesTemporales.get(i));
            sumaNiveles += nivelesTemporales.get(i);
        }

        if (clasesTemporales.isEmpty()) {
            lblClasesAsignadas.setText("Clases (0/4 | Nv.Total 0/20): Ninguna clase añadida");
        } else {
            lblClasesAsignadas.setText("Clases (" + clasesTemporales.size() + "/4 | Nv.Total "
                    + sumaNiveles + "/20): " + texto.toString());
        }
    }

    private void actualizarTablaGremio() {
        DefaultTableModel modelo = (DefaultTableModel) tblGremio.getModel();
        modelo.setRowCount(0);

        ArrayList<Aventurero> lista = controlador.getGremio();
        for (int i = 0; i < lista.size(); i++) {
            Aventurero av = lista.get(i);
            Object[] fila = {
                av.getNombre(),
                av.getTitulo(),
                av.getResumenClases(),
                av.getNivelTotal() + "/20",
                av.getPuntosVidaActual() + "/" + av.getPuntosVidaMax(),
                av.getClaseArmadura(),
                av.getAtributoEspecial()
            };
            modelo.addRow(fila);
        }
    }
    
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtTitulo.setText("");
        txtAtributoEspecial.setText("");
        clasesTemporales.clear();
        nivelesTemporales.clear();
        atributosTemporales.clear();
        actualizarResumenClases();
    }


    private Aventurero construirAventureroDesdeFormulario() throws ReglaDndException {
        String nombre = txtNombre.getText().trim();
        String titulo = txtTitulo.getText().trim();

        if (nombre.isEmpty()) {
            throw new ReglaDndException("Debes ingresar el nombre del aventurero.");
        }
        if (titulo.isEmpty()) {
            titulo = "Aventurero del Gremio";
        }
        if (clasesTemporales.isEmpty()) {
            throw new ReglaDndException("Añade al menos una clase antes de registrar al aventurero.");
        }

        int vidaMax = Integer.parseInt(spnVida.getValue().toString());
        int ca = Integer.parseInt(spnCA.getValue().toString());
        int fue = Integer.parseInt(spnFUE.getValue().toString());
        int des = Integer.parseInt(spnDES.getValue().toString());
        int con = Integer.parseInt(spnCON.getValue().toString());
        int intel = Integer.parseInt(spnINT.getValue().toString());
        int sab = Integer.parseInt(spnSAB.getValue().toString());
        int car = Integer.parseInt(spnCAR.getValue().toString());

        // El héroe se crea una sola vez y luego se le agregan sus clases (composición).
        Aventurero aventurero = new HeroeGremio(nombre, vidaMax, ca,
                fue, des, con, intel, sab, car, titulo);

        for (int i = 0; i < clasesTemporales.size(); i++) {
            ClasePersonaje clase = crearClase(
                    clasesTemporales.get(i), nivelesTemporales.get(i), atributosTemporales.get(i));
            aventurero.agregarClase(clase);
        }

        return aventurero;
    }


/**
     * Crea la ClasePersonaje que corresponde al nombre elegido en el combo,
     * convirtiendo el texto del atributo especial al tipo que necesita cada clase.
     */
    private ClasePersonaje crearClase(String clase, int nivel, String atributo) throws ReglaDndException {
        if (clase.equalsIgnoreCase("Guerrero")) {
            return new Guerrero(nivel, leerSiNo(atributo, "Armadura pesada"));
        } else if (clase.equalsIgnoreCase("Mago")) {
            return new Mago(nivel, leerEntero(atributo, "Espacios de conjuro"));
        } else if (clase.equalsIgnoreCase("Clérigo") || clase.equalsIgnoreCase("Clerigo")) {
            return new Clerigo(nivel, atributo);
        } else if (clase.equalsIgnoreCase("Monje")) {
            return new Monje(nivel, leerEntero(atributo, "Puntos de Ki"));
        } else if (clase.equalsIgnoreCase("Paladín") || clase.equalsIgnoreCase("Paladin")) {
            return new Paladin(nivel, leerEntero(atributo, "Puntos de fuerza"));
        } else if (clase.equalsIgnoreCase("Explorador")) {
            return new Explorador(nivel, atributo);
        } else if (clase.equalsIgnoreCase("Pícaro") || clase.equalsIgnoreCase("Picaro")) {
            return new Picaro(nivel, leerSiNo(atributo, "Herramientas de ladrón"));
        } else if (clase.equalsIgnoreCase("Bardo")) {
            return new Bardo(nivel, atributo);
        } else {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlRegistro = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        spnVida = new javax.swing.JSpinner();
        spnCA = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        spnFUE = new javax.swing.JSpinner();
        spnINT = new javax.swing.JSpinner();
        spnDES = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        spnSAB = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        spnCON = new javax.swing.JSpinner();
        spnCAR = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        cmbClase = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        spnNivelClase = new javax.swing.JSpinner();
        lblAtributoEspecial = new javax.swing.JLabel();
        txtAtributoEspecial = new javax.swing.JTextField();
        btnAgregarClase = new javax.swing.JButton();
        btnLimpiarClases = new javax.swing.JButton();
        lblClasesAsignadas = new javax.swing.JLabel();
        btnRegistrarAventurero = new javax.swing.JButton();
        pnlGremioCombate = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        btnReporteNiveles = new javax.swing.JButton();
        btnSimularCombate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGremio = new javax.swing.JTable();
        btnVerHistorial = new javax.swing.JButton();
        btnReporteMulticlase = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtConsola = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(30, 30, 46));

        jLabel1.setBackground(new java.awt.Color(235, 193, 98));
        jLabel1.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(235, 193, 98));
        jLabel1.setText("WIZARD OF THE COAST- SIMULADOR GREMIO 5 EDICIÓN");

        pnlRegistro.setBackground(new java.awt.Color(43, 55, 94));
        pnlRegistro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jTextField1.setBackground(new java.awt.Color(43, 55, 94));
        jTextField1.setFont(new java.awt.Font("Serif", 1, 16)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(235, 193, 98));
        jTextField1.setText("Registro Aventurero");
        jTextField1.setBorder(null);
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre del aventurero:");

        txtNombre.addActionListener(this::txtNombreActionPerformed);

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Título:");

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Puntos de vida (PG):");

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Clase armadura (CA):");

        jPanel3.setBackground(new java.awt.Color(78, 89, 139));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel6.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(223, 227, 237));
        jLabel6.setText("Características D&D");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("FUERZA:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("INTELIGENCIA:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("DESTREZA:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("SABDURÍA:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("CONSTITUCIÓN:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("CARISMA:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(spnFUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnDES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnCON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnINT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnSAB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnCAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spnFUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(spnDES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(spnCON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(spnINT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(spnSAB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(spnCAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Clase:");

        cmbClase.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbClase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Guerrero", "Mago", "Clérigo", "Monje", "Paladín", "Explorador", "Pícaro", "Bardo" }));
        cmbClase.addActionListener(this::cmbClaseActionPerformed);

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Nivel de esta clase:");

        lblAtributoEspecial.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblAtributoEspecial.setForeground(new java.awt.Color(255, 255, 255));
        lblAtributoEspecial.setText("Espacios de conjuro:");

        txtAtributoEspecial.addActionListener(this::txtAtributoEspecialActionPerformed);

        btnAgregarClase.setBackground(new java.awt.Color(25, 70, 184));
        btnAgregarClase.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAgregarClase.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarClase.setText("+ Añadir clase");
        btnAgregarClase.addActionListener(this::btnAgregarClaseActionPerformed);

        btnLimpiarClases.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLimpiarClases.setText("Limpiar Clases");
        btnLimpiarClases.addActionListener(this::btnLimpiarClasesActionPerformed);

        btnRegistrarAventurero.setBackground(new java.awt.Color(154, 9, 31));
        btnRegistrarAventurero.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrarAventurero.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarAventurero.setText("REGISTRAR AVENTURERO EN EL GREMIO");
        btnRegistrarAventurero.addActionListener(this::btnRegistrarAventureroActionPerformed);

        javax.swing.GroupLayout pnlRegistroLayout = new javax.swing.GroupLayout(pnlRegistro);
        pnlRegistro.setLayout(pnlRegistroLayout);
        pnlRegistroLayout.setHorizontalGroup(
            pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRegistroLayout.createSequentialGroup()
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRegistroLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblClasesAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlRegistroLayout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(spnVida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spnCA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlRegistroLayout.createSequentialGroup()
                                    .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(pnlRegistroLayout.createSequentialGroup()
                                    .addGap(53, 53, 53)
                                    .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(pnlRegistroLayout.createSequentialGroup()
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(cmbClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(spnNivelClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlRegistroLayout.createSequentialGroup()
                                            .addComponent(lblAtributoEspecial)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtAtributoEspecial, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(12, 12, 12))))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistroLayout.createSequentialGroup()
                                    .addComponent(btnAgregarClase, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnLimpiarClases)
                                    .addGap(22, 22, 22)))))
                    .addGroup(pnlRegistroLayout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(btnRegistrarAventurero)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        pnlRegistroLayout.setVerticalGroup(
            pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRegistroLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spnVida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(spnCA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cmbClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(spnNivelClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAtributoEspecial)
                    .addComponent(txtAtributoEspecial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiarClases)
                    .addComponent(btnAgregarClase))
                .addGap(18, 18, 18)
                .addComponent(lblClasesAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRegistrarAventurero)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        pnlGremioCombate.setBackground(new java.awt.Color(43, 55, 94));
        pnlGremioCombate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jTextField2.setBackground(new java.awt.Color(43, 55, 94));
        jTextField2.setFont(new java.awt.Font("Serif", 1, 16)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(235, 193, 98));
        jTextField2.setText("Aventureros del gremio");
        jTextField2.setBorder(null);
        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        btnReporteNiveles.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReporteNiveles.setText("Reporte niveles");
        btnReporteNiveles.addActionListener(this::btnReporteNivelesActionPerformed);

        btnSimularCombate.setBackground(new java.awt.Color(154, 9, 31));
        btnSimularCombate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSimularCombate.setForeground(new java.awt.Color(255, 255, 255));
        btnSimularCombate.setText("SIMULAR COMBATE");
        btnSimularCombate.addActionListener(this::btnSimularCombateActionPerformed);

        tblGremio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Aventurero", "Clases y niveles", "Nivel total", "PG", "CA"
            }
        ));
        jScrollPane2.setViewportView(tblGremio);
        tblGremio.getAccessibleContext().setAccessibleParent(jScrollPane1);

        jScrollPane1.setViewportView(jScrollPane2);

        btnVerHistorial.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVerHistorial.setText("Ver registro_gremio.txt");
        btnVerHistorial.setActionCommand("Reporte ");
        btnVerHistorial.addActionListener(this::btnVerHistorialActionPerformed);

        btnReporteMulticlase.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReporteMulticlase.setText("Reporte multiclase");
        btnReporteMulticlase.setActionCommand("Reporte ");
        btnReporteMulticlase.addActionListener(this::btnReporteMulticlaseActionPerformed);

        txtConsola.setColumns(20);
        txtConsola.setRows(5);
        jScrollPane4.setViewportView(txtConsola);

        javax.swing.GroupLayout pnlGremioCombateLayout = new javax.swing.GroupLayout(pnlGremioCombate);
        pnlGremioCombate.setLayout(pnlGremioCombateLayout);
        pnlGremioCombateLayout.setHorizontalGroup(
            pnlGremioCombateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGremioCombateLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnlGremioCombateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlGremioCombateLayout.createSequentialGroup()
                        .addComponent(btnSimularCombate, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnReporteNiveles)
                        .addGap(18, 18, 18)
                        .addComponent(btnReporteMulticlase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVerHistorial))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        pnlGremioCombateLayout.setVerticalGroup(
            pnlGremioCombateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGremioCombateLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlGremioCombateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReporteNiveles)
                    .addComponent(btnReporteMulticlase)
                    .addComponent(btnVerHistorial)
                    .addComponent(btnSimularCombate))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(pnlRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlGremioCombate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(282, 282, 282)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGremioCombate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void cmbClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClaseActionPerformed
        // TODO add your handling code here:
        actualizarEtiquetaAtributo();
    }//GEN-LAST:event_cmbClaseActionPerformed

    private void txtAtributoEspecialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAtributoEspecialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAtributoEspecialActionPerformed

    private void btnRegistrarAventureroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarAventureroActionPerformed
        // TODO add your handling code here:
        try {
            Aventurero nuevo = construirAventureroDesdeFormulario();
            controlador.registrarAventurero(nuevo);
            actualizarTablaGremio();
            limpiarFormulario();
            txtConsola.setText(controlador.generarReporteGremio() + "\n" + controlador.generarReporteCombinaciones());
            JOptionPane.showMessageDialog(
                    this,
                    "Aventurero registrado correctamente en el gremio.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (ReglaDndException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Regla D&D 5e", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Revisa que los campos numéricos sean válidos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRegistrarAventureroActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void btnSimularCombateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimularCombateActionPerformed
        // TODO add your handling code here:
        try {
            String logCombate = controlador.simularCombate();
            actualizarTablaGremio();
            txtConsola.setText(logCombate);
        } catch (ReglaDndException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Combate", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSimularCombateActionPerformed

    private void btnReporteNivelesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteNivelesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnReporteNivelesActionPerformed

    private void btnVerHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerHistorialActionPerformed
        // TODO add your handling code here:
        txtConsola.setText(GestorPersistencia.leerHistorial());
    }//GEN-LAST:event_btnVerHistorialActionPerformed

    private void btnReporteMulticlaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteMulticlaseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnReporteMulticlaseActionPerformed

    private void btnAgregarClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClaseActionPerformed
        // TODO add your handling code here:
        try {
            if (clasesTemporales.size() >= 4) {
                throw new ReglaDndException("Un aventurero solo puede tener hasta 4 clases.");
            }
            String clase = cmbClase.getSelectedItem().toString();
            int nivelClase = Integer.parseInt(spnNivelClase.getValue().toString());
            String atributo = txtAtributoEspecial.getText().trim();

            if (nivelClase <= 0) {
                throw new ReglaDndException("El nivel de la clase debe ser mayor que 0.");
            }
            if (atributo.isEmpty()) {
                throw new ReglaDndException("Debes indicar el atributo especial de la clase.");
            }

            int sumaActual = 0;
            for (int i = 0; i < nivelesTemporales.size(); i++) {
                sumaActual += nivelesTemporales.get(i);
            }
            if (sumaActual + nivelClase > 20) {
                throw new ReglaDndException("El nivel total del aventurero no puede superar 20.");
            }

            clasesTemporales.add(clase);
            nivelesTemporales.add(nivelClase);
            atributosTemporales.add(atributo);
            txtAtributoEspecial.setText("");
            actualizarResumenClases();
        } catch (ReglaDndException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnAgregarClaseActionPerformed

    private void btnLimpiarClasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarClasesActionPerformed
        // TODO add your handling code here:
        clasesTemporales.clear();
        nivelesTemporales.clear();
        atributosTemporales.clear();
        actualizarResumenClases();
    }//GEN-LAST:event_btnLimpiarClasesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VistaGremio().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarClase;
    private javax.swing.JButton btnLimpiarClases;
    private javax.swing.JButton btnRegistrarAventurero;
    private javax.swing.JButton btnReporteMulticlase;
    private javax.swing.JButton btnReporteNiveles;
    private javax.swing.JButton btnSimularCombate;
    private javax.swing.JButton btnVerHistorial;
    private javax.swing.JComboBox<String> cmbClase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblAtributoEspecial;
    private javax.swing.JLabel lblClasesAsignadas;
    private javax.swing.JPanel pnlGremioCombate;
    private javax.swing.JPanel pnlRegistro;
    private javax.swing.JSpinner spnCA;
    private javax.swing.JSpinner spnCAR;
    private javax.swing.JSpinner spnCON;
    private javax.swing.JSpinner spnDES;
    private javax.swing.JSpinner spnFUE;
    private javax.swing.JSpinner spnINT;
    private javax.swing.JSpinner spnNivelClase;
    private javax.swing.JSpinner spnSAB;
    private javax.swing.JSpinner spnVida;
    private javax.swing.JTable tblGremio;
    private javax.swing.JTextField txtAtributoEspecial;
    private javax.swing.JTextArea txtConsola;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration//GEN-END:variables
}
