/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.painter.CompoundPainter;
import java.util.List;


/**
 *
 * @author Umadc
 */

public class mapa extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(mapa.class.getName());
    
    // Lista para almacenar los puntos de siembra manteniendo su orden (A, B, C...)
    private List<Waypoint> puntosDeSiembra; 
    private JXMapViewer mapViewer;
    private Waypoint puntoArrastrado = null; // Variable para gestionar qué punto se está moviendo
    private PanMouseInputListener panListener; // Listener para poder mover (panear) el mapa

    public mapa() {
        initComponents(); // Primero inicializamos lo que viene del diseñador
        
        // Configuraciones básicas de la ventana
        this.setTitle("Sistema de Mapeo de Terrenos");
        this.setSize(1000, 600);
        
        // 1. Aseguramos el layout del contenedor principal
        this.setLayout(new BorderLayout());
        
        // 2. Le damos un ancho fijo a tu panel lateral diseñado en NetBeans
        jPanel1.setPreferredSize(new Dimension(300, 0));
        jPanel1.setMinimumSize(new Dimension(300, 0));
        jPanel1.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        // 3. Añadimos los paneles usando BorderLayout
        this.add(panelMapa, BorderLayout.CENTER); // Mapa al centro
        this.add(jPanel1, BorderLayout.EAST);     // Tu panel a la derecha
        this.add(jPanel3, BorderLayout.SOUTH);  // Tu panel que está en la parte baja 
        inicializarMapa();
        
        // 4. Forzamos la actualización de la interfaz
        this.revalidate();
        this.repaint();
    }

    private void inicializarMapa() {
        mapViewer = new JXMapViewer();

        // Configuración de la fuente de imágenes satelitales (Esri)
        TileFactoryInfo info = new TileFactoryInfo(1, 19, 19, 256, true, true, 
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile", "x", "y", "z") {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 19 - zoom; // Ajuste para el límite de zoom definido
                return this.baseURL + "/" + z + "/" + y + "/" + x;
            }
        };

        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setAddressLocation(new GeoPosition(21.814398, -102.771391)); // Punto inicial
        mapViewer.setZoom(2); // Nivel de zoom inicial

        // Usamos ArrayList para mantener el orden de los vértices
        puntosDeSiembra = new ArrayList<>();

        // Pintor 1: Dibuja el polígono verde que une los puntos
        Painter<JXMapViewer> areaPainter = (g, map, w, h) -> {
            if (puntosDeSiembra.size() < 2) return;
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            g.translate(-rect.x, -rect.y);
            
            Path2D.Double path = new Path2D.Double();
            boolean first = true;
            for (Waypoint wp : puntosDeSiembra) {
                Point2D pt = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                if (first) { path.moveTo(pt.getX(), pt.getY()); first = false; }
                else { path.lineTo(pt.getX(), pt.getY()); }
            }
            path.closePath();
            g.setColor(new Color(46, 204, 113, 80)); // Relleno verde semitransparente
            g.fill(path);
            g.setColor(new Color(39, 174, 96, 220)); // Borde del polígono
            g.setStroke(new BasicStroke(2.5f));
            g.draw(path);
            g.dispose();
        };

        // Pintor 2: Dibuja los círculos azules y sus etiquetas (A, B, C...)
        Painter<JXMapViewer> nodePainter = (g, map, w, h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            g.translate(-rect.x, -rect.y);
            int idx = 0;
            for (Waypoint wp : puntosDeSiembra) {
                Point2D pt = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                g.setColor(new Color(41, 128, 185)); // Color del nodo
                g.fillOval((int)pt.getX() - 8, (int)pt.getY() - 8, 16, 16);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf((char)('A' + (idx++ % 26))), (int)pt.getX() + 12, (int)pt.getY() + 5);
            }
            g.dispose();
        };

        // Combinar pintores para mostrar área y puntos simultáneamente
        mapViewer.setOverlayPainter(new CompoundPainter<>(areaPainter, nodePainter));

        // Configuración de controles de navegación (Paneo y Zoom con rueda)
        panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // Listener para acciones de edición de puntos
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Al presionar Ctrl + Clic izquierdo, intentamos seleccionar un punto para mover
                if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
                    GeoPosition pos = mapViewer.convertPointToGeoPosition(e.getPoint());
                    puntoArrastrado = encontrarPuntoCercano(pos);
                    if (puntoArrastrado != null) {
                        // Desactivar paneo para evitar conflicto al mover el punto
                        mapViewer.removeMouseListener(panListener);
                        mapViewer.removeMouseMotionListener(panListener);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Si estamos arrastrando un punto, actualizamos su coordenada
                if (puntoArrastrado != null) {
                    int index = puntosDeSiembra.indexOf(puntoArrastrado);
                    puntoArrastrado = new DefaultWaypoint(mapViewer.convertPointToGeoPosition(e.getPoint()));
                    puntosDeSiembra.set(index, puntoArrastrado);
                    mapViewer.repaint();
                    actualizarCalculos();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Al soltar el mouse, reactivamos el paneo del mapa
                if (puntoArrastrado != null) {
                    mapViewer.addMouseListener(panListener);
                    mapViewer.addMouseMotionListener(panListener);
                    puntoArrastrado = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Doble clic izquierdo: Agregar nuevo punto
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    puntosDeSiembra.add(new DefaultWaypoint(mapViewer.convertPointToGeoPosition(e.getPoint())));
                } 
                // Clic derecho: Eliminar último punto
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!puntosDeSiembra.isEmpty()) {
                        puntosDeSiembra.remove(puntosDeSiembra.size() - 1);
                    }
                } 
                // Clic central (rueda): Limpiar todo
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    puntosDeSiembra.clear();
                }
                mapViewer.repaint();
                actualizarCalculos();
            }
        };

        mapViewer.addMouseListener(ma);
        mapViewer.addMouseMotionListener(ma);

        panelMapa.setLayout(new BorderLayout());
        panelMapa.add(mapViewer, BorderLayout.CENTER);
    }

    private void actualizarCalculos() {
        // Si no hay suficientes puntos, reiniciamos las etiquetas
        if (puntosDeSiembra.size() < 2) {
            jLabel8.setText("0.0");  // Área
            jLabel13.setText("0.0"); // Perímetro
            return;
        }

        int n = puntosDeSiembra.size();
        double[] lats = new double[n];
        double[] lons = new double[n];

        for (int i = 0; i < n; i++) {
            GeoPosition pos = puntosDeSiembra.get(i).getPosition();
            lats[i] = pos.getLatitude();
            lons[i] = pos.getLongitude();
        }

        // 1. CÁLCULO DEL ÁREA
        calculo calc = new calculo("Terreno Actual", lats, lons);
        double area = calc.calcularAreaIntegral();
        jLabel8.setText(String.format(java.util.Locale.US, "%.2f", area));

        // 2. CÁLCULO DEL PERÍMETRO (Nueva lógica)
        double perimetroMeters = 0.0;
        double metrosPorGrado = 111320.0;

        for (int i = 0; i < n; i++) {
            int sig = (i + 1) % n; // Siguiente punto (vuelve al primero al final)

            // Diferencia en grados
            double dLat = lats[sig] - lats[i];
            double dLon = lons[sig] - lons[i];

            // Conversión aproximada a metros (Teorema de Pitágoras plano)
            double metrosLat = dLat * metrosPorGrado;
            double metrosLon = dLon * metrosPorGrado;

            perimetroMeters += Math.sqrt((metrosLat * metrosLat) + (metrosLon * metrosLon));
        }

        // Mostrar el perímetro en jLabel13 con 2 decimales
        jLabel13.setText(String.format(java.util.Locale.US, "%.2f", perimetroMeters));
        
        // 3. SIMULACIÓN DE VALORES TÉRMICOS (Añadir al final de actualizarCalculos)
        // Genera valores simulados pero realistas para la demostración
        double tempSimulada = 22.0 + (Math.random() * 8.0); // Entre 22°C y 30°C
        double humSimulada = 45.0 + (Math.random() * 20.0); // Entre 45% y 65%

        jLabel5.setText(String.format(java.util.Locale.US, "%.1f", tempSimulada)); // Temperatura
        jLabel3.setText(String.format(java.util.Locale.US, "%.1f", humSimulada));  // Humedad
    }
    
    // Método auxiliar para detectar si hicimos clic sobre un punto existente
    private Waypoint encontrarPuntoCercano(GeoPosition pos) {
        for (Waypoint wp : puntosDeSiembra) {
            Point2D p1 = mapViewer.getTileFactory().geoToPixel(pos, mapViewer.getZoom());
            Point2D p2 = mapViewer.getTileFactory().geoToPixel(wp.getPosition(), mapViewer.getZoom());
            if (p1.distance(p2) < 20) return wp;
        }
        return null;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        panelMapa = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        chkTomato = new javax.swing.JCheckBox();
        chkCucumber = new javax.swing.JCheckBox();
        btnCalculateAndSave = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtTerrainName = new javax.swing.JTextField();
        btnClearMap = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setLocation(new java.awt.Point(250, 75));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(170, 114, 41));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Características del terreno");
        jLabel1.setAlignmentX(0.5F);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Humedad: ");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("--");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Temperatura:");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("--");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("________________________________");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Área:");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setText("0.0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setForeground(new java.awt.Color(189, 189, 189));
        jLabel9.setText("________________________________");

        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setText("%");

        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("°C");

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("m²");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("0.0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setForeground(new java.awt.Color(189, 189, 189));
        jLabel14.setText("________________________________");

        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setText("m");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Preímetro:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel6)
                            .addComponent(jLabel14))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addGap(31, 31, 31))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(33, 33, 33))))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10))
                .addGap(2, 2, 2)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMapaLayout = new javax.swing.GroupLayout(panelMapa);
        panelMapa.setLayout(panelMapaLayout);
        panelMapaLayout.setHorizontalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 761, Short.MAX_VALUE)
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(170, 114, 41));
        jLabel17.setText("Panel de Control de Cultivo");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(110, 125, 78));
        jLabel18.setText("Nombre del terreno:");

        jLabel19.setForeground(new java.awt.Color(110, 125, 78));
        jLabel19.setText("Selecciona cultivo para estimar espacio:");

        chkTomato.setText("Jitomate (45cm)");
        chkTomato.addActionListener(this::chkTomatoActionPerformed);

        chkCucumber.setText("Pepino (30cm)");
        chkCucumber.addActionListener(this::chkCucumberActionPerformed);

        btnCalculateAndSave.setBackground(new java.awt.Color(110, 125, 78));
        btnCalculateAndSave.setForeground(new java.awt.Color(255, 255, 255));
        btnCalculateAndSave.setText("Calcular, Filtrar y Guardar");
        btnCalculateAndSave.addActionListener(this::btnCalculateAndSaveActionPerformed);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(170, 114, 41));
        jLabel20.setText("Guía de Siembra Activa");

        jTable1.setBackground(new java.awt.Color(216, 224, 199));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Jitomate", "45cm"},
                {"Pepino", "30 cm"},
                {null, null},
                {null, null}
            },
            new String [] {
                "Cultivo", "Distancia Mínima Requerida"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        btnClearMap.setBackground(new java.awt.Color(170, 114, 40));
        btnClearMap.setForeground(new java.awt.Color(255, 255, 255));
        btnClearMap.setText("Limpiar Mapa");
        btnClearMap.addActionListener(this::btnClearMapActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTerrainName))
                    .addComponent(jLabel17)
                    .addComponent(jLabel19)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkCucumber, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCalculateAndSave))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chkTomato, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClearMap)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtTerrainName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkTomato)
                            .addComponent(btnClearMap))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkCucumber)
                            .addComponent(btnCalculateAndSave)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkTomatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTomatoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkTomatoActionPerformed

    private void chkCucumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCucumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCucumberActionPerformed

    private void btnCalculateAndSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateAndSaveActionPerformed
    String terrainName = txtTerrainName.getText().trim();
    
    if (terrainName.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "⚠️ ALERT: Please enter a terrain name first!", 
            "Validation Error", 
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. FILTER AND PROCESS CROP SELECTION
    String selectedCrop = "";
    String requiredDistance = "";

    if (chkTomato.isSelected()) {
        selectedCrop = "Tomato";
        requiredDistance = "40-50cm";
    } else if (chkCucumber.isSelected()) {
        selectedCrop = "Cucumber";
        requiredDistance = "25-35cm";
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "⚠️ ALERT: You must select a crop (Tomato or Cucumber)!", 
            "Validation Error", 
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3. TELEMETRY SENSOR SIMULATION (Live Math)
    double sensorMoisture = 30.0 + (Math.random() * 55.0);
    sensorMoisture = Math.round(sensorMoisture * 10.0) / 10.0;

    // 4. VISUAL ACTION: Pop-up report on screen
    String summaryReport = "==================================\n"
                         + "        CROP MAPPING & SENSOR REPORT      \n"
                         + "==================================\n"
                         + "• Terrain Name: " + terrainName.toUpperCase() + "\n"
                         + "• Selected Crop: " + selectedCrop + "\n"
                         + "• Required Distance: " + requiredDistance + "\n"
                         + "--------------------------------------------------\n"
                         + "📡 LIVE SENSOR TELEMETRY:\n"
                         + "• Soil Moisture Level: " + sensorMoisture + "%\n"
                         + "==================================\n"
                         + "Data calculated and processed successfully!";

    javax.swing.JOptionPane.showMessageDialog(this, 
        summaryReport, 
        "System Simulation Success", 
        javax.swing.JOptionPane.INFORMATION_MESSAGE);

    // 5. DATABASE ASYNC LOGGING
    String sql = "INSERT INTO crop_mapping (terrain_name, crop, minimum_distance, sensor_moisture) VALUES (?, ?, ?, ?)";        // TODO add your handling code here:
    }//GEN-LAST:event_btnCalculateAndSaveActionPerformed

    private void btnClearMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearMapActionPerformed
        // Clears the text box using its new English name
        txtTerrainName.setText("");

        // Unchecks both crop checkboxes using their new names
        chkTomato.setSelected(false);
        chkCucumber.setSelected(false);        // TODO add your handling code here:
    }//GEN-LAST:event_btnClearMapActionPerformed
    // Variables declaration - do not modify                     
    // End of variables declaration                   
     
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculateAndSave;
    private javax.swing.JButton btnClearMap;
    private javax.swing.JCheckBox chkCucumber;
    private javax.swing.JCheckBox chkTomato;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel panelMapa;
    private javax.swing.JTextField txtTerrainName;
    // End of variables declaration//GEN-END:variables
}
