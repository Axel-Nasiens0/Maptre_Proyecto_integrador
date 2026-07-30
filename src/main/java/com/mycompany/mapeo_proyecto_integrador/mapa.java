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
import javax.swing.JComponent;
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
import javax.swing.JOptionPane;
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
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"1. Tomate", "40-50 cm"});
        model.addRow(new Object[]{"2. Lechuga", "25-30 cm"});


        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setShowVerticalLines(true);
        jTable1.setGridColor(new java.awt.Color(180, 170, 160));
        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(50, 50, 50));
        jTable1.setRowHeight(30);
        jTable1.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        
        jButton5.setContentAreaFilled(true); 
        jButton5.setBorderPainted(false);     
        jButton5.setFocusPainted(false);
        
        jButton1.setContentAreaFilled(true); 
        jButton1.setBorderPainted(false);     
        jButton1.setFocusPainted(false);

        jButton2.setContentAreaFilled(true); 
        jButton2.setBorderPainted(false);     
        jButton2.setFocusPainted(false);
        
        jButton3.setContentAreaFilled(true); 
        jButton3.setBorderPainted(false);     
        jButton3.setFocusPainted(false);
        
        jButton2.putClientProperty("FlatLaf.style", ""
                + "background: #330000;"
                + "foreground: #FFFFFF;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "arc: 999;");

        // Configuraciones básicas de la ventana
        this.setTitle("Sistema de Mapeo de Terrenos");
        this.setSize(1000, 600);

        // 1. Aseguramos el layout del contenedor principal
        this.setLayout(new BorderLayout());

        // 2. Mantenemos las dimensiones fijas del panel lateral
        jPanel1.setPreferredSize(new Dimension(300, 0));
        jPanel1.setMinimumSize(new Dimension(300, 0));
        jPanel1.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        // Ocultamos el panel al inicio
        jPanel1.setVisible(false);        
        
        // Le asignas un GridLayout de 1 fila y 3 columnas
        jPanel5.setLayout(new java.awt.GridLayout(1, 3, 10, 0)); // 10px de espacio horizontal opcional

        // Mantenemos la altura fija de la barra inferior
        jPanel5.setPreferredSize(new Dimension(this.getWidth(), 50)); 

        // 3. Añadimos los paneles manteniendo el layout actual
        this.add(panelMapa, BorderLayout.CENTER); 
        this.add(jPanel1, BorderLayout.EAST);     
        this.add(jPanel5, BorderLayout.SOUTH);    

        JComponent glass = (JComponent) this.getGlassPane();
        glass.setLayout(null); 

        // 2. Sacamos el jButton2 de donde esté y lo metemos en la capa suprema
        glass.add(jButton2);

        // 3. Lo posicionamos exactamente en la esquina superior izquierda
        jButton2.setBounds(20, 20, 90, 35); 
        
        // 4. Activamos la capa para que sea visible por encima del mapa
        glass.setVisible(true);

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
                if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
                    GeoPosition pos = mapViewer.convertPointToGeoPosition(e.getPoint());
                    puntoArrastrado = encontrarPuntoCercano(pos);
                    if (puntoArrastrado != null) {
                        mapViewer.removeMouseListener(panListener);
                        mapViewer.removeMouseMotionListener(panListener);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
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
                if (puntoArrastrado != null) {
                    mapViewer.addMouseListener(panListener);
                    mapViewer.addMouseMotionListener(panListener);
                    actualizarClimaApi(puntoArrastrado.getPosition()); // Llamada directa simplificada
                    puntoArrastrado = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    puntosDeSiembra.add(new DefaultWaypoint(mapViewer.convertPointToGeoPosition(e.getPoint())));
                    if (!puntosDeSiembra.isEmpty()) { 
                        actualizarClimaApi(puntosDeSiembra.get(puntosDeSiembra.size() - 1).getPosition()); 
                    }
                } 
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!puntosDeSiembra.isEmpty()) {
                        puntosDeSiembra.remove(puntosDeSiembra.size() - 1);
                        if (!puntosDeSiembra.isEmpty()) { 
                            actualizarClimaApi(puntosDeSiembra.get(puntosDeSiembra.size() - 1).getPosition()); 
                        } else { 
                            jLabel5.setText("--"); 
                            jLabel3.setText("--"); 
                        }
                    } else {
                        jLabel5.setText("--"); 
                        jLabel3.setText("--");
                    }
                } 
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    puntosDeSiembra.clear();
                    jLabel5.setText("--"); 
                    jLabel3.setText("--"); 
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
    
 
    public void actualizarClimaApi(GeoPosition pos) {
        if (pos == null) return;
        jLabel5.setText("...");
        jLabel3.setText("...");
        new Thread(() -> {
            try {
                String urlString = String.format(java.util.Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m", 
                    pos.getLatitude(), pos.getLongitude());
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) { sb.append(line); }
                    br.close(); conn.disconnect();
                    
                  
                    org.json.JSONObject json = new org.json.JSONObject(sb.toString());
                    org.json.JSONObject current = json.getJSONObject("current");
                    final double temp = current.getDouble("temperature_2m");
                    final double hum = current.getDouble("relative_humidity_2m");
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        jLabel5.setText(String.format(java.util.Locale.US, "%.1f", temp));
                        jLabel3.setText(String.format(java.util.Locale.US, "%.0f", hum));
                    });
                } else { conn.disconnect(); }
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    jLabel5.setText("Err");
                    jLabel3.setText("Err");
                });
            }
        }).start();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMapa = new javax.swing.JPanel();
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
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setLocation(new java.awt.Point(250, 75));
        setUndecorated(true);

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

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(170, 114, 41));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Vegetales y Medidas");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("<html>medidas para que el cultivo se dé de manera<br><center>saludable</center></html>");

        jTable1.setBorder(new javax.swing.border.MatteBorder(null));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Tomate ", "40-50 cm"},
                {"Lechuga", "25-30 cm"},
                {null, null},
                {null, null}
            },
            new String [] {
                "Vegetales", "Medidas"
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

        jCheckBox1.setText("Lechuga");

        jCheckBox2.setText("Tomate");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(33, 33, 33))))
            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addGap(24, 24, 24)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jButton5.setForeground(new java.awt.Color(170, 114, 41));
        jButton5.setText("Características");
        jButton5.setBorder(null);
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton5MouseExited(evt);
            }
        });
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton1.setForeground(new java.awt.Color(170, 114, 41));
        jButton1.setText("Opciones de guardado");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton3.setForeground(new java.awt.Color(170, 114, 41));
        jButton3.setText("Otras opciones");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton3MouseExited(evt);
            }
        });
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 281, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(44, 44, 44))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setBackground(new java.awt.Color(51, 0, 0));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Exit");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton2MouseExited(evt);
            }
        });
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jPanel3.setBackground(new java.awt.Color(245, 245, 245));
        jPanel3.setName("Nombre del Terreno"); // NOI18N

        jTextField1.setBackground(new java.awt.Color(204, 204, 204));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jButton4.setBackground(new java.awt.Color(102, 204, 0));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Guardar");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton6.setBackground(new java.awt.Color(255, 51, 0));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Borrar");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jButton6)
                .addGap(11, 11, 11))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton6))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMapaLayout = new javax.swing.GroupLayout(panelMapa);
        panelMapa.setLayout(panelMapaLayout);
        panelMapaLayout.setHorizontalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addGroup(panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMapaLayout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(270, 270, 270))
                    .addGroup(panelMapaLayout.createSequentialGroup()
                        .addGap(224, 224, 224)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(114, 114, 114)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseEntered
        // TODO add your handling code here:
        jButton2.setBackground(Color.decode("#FFFFFF"));
        jButton2.setForeground(Color.decode("#FF3333"));
    }//GEN-LAST:event_jButton2MouseEntered

    private void jButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseExited
        // TODO add your handling code here:
        jButton2.setBackground(Color.decode("#330000"));
        jButton2.setForeground(Color.decode("#FFFFFF"));
    }//GEN-LAST:event_jButton2MouseExited

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // Invertimos el estado de visibilidad actual del panel
        boolean estadoActual = jPanel1.isVisible();
        jPanel1.setVisible(!estadoActual);
        // CRUCIAL: Le avisamos al layout que el espacio en pantalla cambió
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseEntered
        // TODO add your handling code here:
        jButton5.setBackground(Color.decode("#F2E6D8"));
    }//GEN-LAST:event_jButton5MouseEntered

    private void jButton5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseExited
        // TODO add your handling code here:
        jButton5.setBackground(Color.decode("#FFFFFF"));
    }//GEN-LAST:event_jButton5MouseExited

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        // TODO add your handling code here:
        jButton1.setBackground(Color.decode("#F2E6D8"));
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        // TODO add your handling code here:
        jButton1.setBackground(Color.decode("#FFFFFF"));
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseEntered
        // TODO add your handling code here:
        jButton3.setBackground(Color.decode("#F2E6D8"));
    }//GEN-LAST:event_jButton3MouseEntered

    private void jButton3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseExited
        // TODO add your handling code here:
        jButton3.setBackground(Color.decode("#FFFFFF"));
    }//GEN-LAST:event_jButton3MouseExited

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    // 1. Vaciar la lista de puntos
    if (puntosDeSiembra != null) {
        puntosDeSiembra.clear();
    }
    
    // 2. Limpiar la caja del nombre del terreno
    jTextField1.setText("");
    
    // 3. Resetear las etiquetas de Área y Perímetro
    jLabel3.setText("0.00 m²");
    jLabel5.setText("0.00 m");
    
    // 4. Volver a pintar/refrescar el mapa para que desaparezcan las líneas y marcadores
    if (mapViewer != null) {
        mapViewer.repaint();
    }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
      
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
String nombreTerreno = jTextField1.getText().trim();
     // 1. Validar que no esté vacío el nombre del terreno
    if (nombreTerreno.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Por favor ingrese el nombre del terreno.", 
            "Atención", 
            JOptionPane.WARNING_MESSAGE);
        return;
    } 

    // 2. Advertencia si no hay más de 3 puntos
    if (puntosDeSiembra == null || puntosDeSiembra.size() < 3) {
        JOptionPane.showMessageDialog(this, 
            "Debes colocar más de tres puntos en el mapa para delimitar el terreno.", 
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
        return; // Detiene la ejecución para que no guarde
    }
    

    // 3. Ventana de éxito
    JOptionPane.showMessageDialog(this, 
        "Terreno '" + nombreTerreno + "' guardado con éxito.", 
        "Éxito", 
        JOptionPane.INFORMATION_MESSAGE);

    // 4. Limpieza automática de la pantalla para ingresar otro terreno
    if (puntosDeSiembra != null) {
        puntosDeSiembra.clear();
    }
    jTextField1.setText("");
    jLabel3.setText("0.00 m²");
    jLabel5.setText("0.00 m");
    
    jCheckBox1.setSelected(false);
    jCheckBox2.setSelected(false); 

    if (mapViewer != null) {
        mapViewer.repaint();
       
       
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel panelMapa;
    // End of variables declaration//GEN-END:variables
}
