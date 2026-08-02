/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Umadc
 */

public class MapFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MapFrame.class.getName());
    
    private List<Waypoint> plantingPoints; 
    private JXMapViewer mapViewer;
    private Waypoint draggedPoint = null; 
    private PanMouseInputListener panListener;

    // CardLayout Container Components
    private CardLayout containerLayout;
    private JPanel imageViewPanel;
    private JLabel imageViewerLabel;
    private JButton btnReturnToMap;
    
    private javax.swing.JTable jTable1;
    
    private javax.swing.JCheckBox chkCarrot = new javax.swing.JCheckBox();
    private javax.swing.JCheckBox chkRadish = new javax.swing.JCheckBox();
    private javax.swing.JCheckBox chkTomato = new javax.swing.JCheckBox();
    private javax.swing.JCheckBox chkCorn = new javax.swing.JCheckBox();
    private javax.swing.JCheckBox chkBean = new javax.swing.JCheckBox();
    
    private InteractiveImagePanel interactiveImagePanel;

    // Default Constructor
    public MapFrame() {
        this(null);
    }

    // Main Constructor with optional initial image
    public MapFrame(String initialImageName) {
        initComponents();

        // Create model and table
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][] {}, 
            new String[] { "Sel.", "Vegetable", "In-Row Spacing", "Row Spacing" }
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // Visual Checkbox rendering
                return super.getColumnClass(columnIndex);
            }
        };

        jTable1 = new javax.swing.JTable(model);

        // Assign unique names to each variable for identification
        chkCarrot.setName("chkCarrot");
        chkRadish.setName("chkRadish");
        chkTomato.setName("chkTomato");
        chkCorn.setName("chkCorn");
        chkBean.setName("chkBean");

        // Add rows (initially set to false based on component state)
        model.addRow(new Object[]{chkCarrot.isSelected(), "Carrot", "5 cm - 10 cm", "20 cm"});
        model.addRow(new Object[]{chkRadish.isSelected(), "Radish", "5 cm", "--"});
        model.addRow(new Object[]{chkTomato.isSelected(), "Tomato", "50 cm", "60 cm - 100 cm"});
        model.addRow(new Object[]{chkCorn.isSelected(), "Corn", "20 cm - 40 cm", "75 cm"});
        model.addRow(new Object[]{chkBean.isSelected(), "Bean", "10 cm - 15 cm", "50 cm - 80 cm"});

        // Synchronize table clicks with corresponding individual variables
        model.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                boolean val = (Boolean) model.getValueAt(row, 0);
                switch (row) {
                    case 0 -> chkCarrot.setSelected(val);
                    case 1 -> chkRadish.setSelected(val);
                    case 2 -> chkTomato.setSelected(val);
                    case 3 -> chkCorn.setSelected(val);
                    case 4 -> chkBean.setSelected(val);
                }
            }
        });

        // Table styling
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setShowVerticalLines(true);
        jTable1.setGridColor(new java.awt.Color(180, 170, 160));
        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(50, 50, 50));
        jTable1.setRowHeight(30);

        // Create the ScrollPane
        javax.swing.JScrollPane tableScrollPane = new javax.swing.JScrollPane(jTable1);
        tableScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(java.awt.Color.WHITE);
        tableScrollPane.setPreferredSize(new java.awt.Dimension(280, 180));
        tableScrollPane.setBounds(10, 230, 280, 250);

        // Add the table DIRECTLY to jPanel1
        jPanel1.add(tableScrollPane);

        // Refresh the panel
        jPanel1.revalidate();
        jPanel1.repaint();

        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(35);  // Sel.
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);  // Vegetable
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(110); // In-Row Spacing
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(110); // Row Spacing

        jButton5.setContentAreaFilled(true); 
        jButton5.setBorderPainted(false);     
        jButton5.setFocusPainted(false);

        jButton1.setContentAreaFilled(true); 
        jButton1.setBorderPainted(false);     
        jButton1.setFocusPainted(false);

        jButton2.setContentAreaFilled(true); 
        jButton2.setBorderPainted(false);     
        jButton2.setFocusPainted(false);
        
        jButton4.setContentAreaFilled(true); 
        jButton4.setBorderPainted(false);     
        jButton4.setFocusPainted(false);
        
        jButton6.setContentAreaFilled(true); 
        jButton6.setBorderPainted(false);     
        jButton6.setFocusPainted(false);

        jButton2.putClientProperty("FlatLaf.style", ""
                + "background: #330000;"
                + "foreground: #FFFFFF;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "arc: 999;");

        jButton4.putClientProperty("FlatLaf.style", ""
                + "background: #6B8E23;"
                + "foreground: #FFFFFF;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "arc: 999;");
        
        jButton6.putClientProperty("FlatLaf.style", ""
                + "background: #B22222;"
                + "foreground: #FFFFFF;"
                + "borderWidth: 0;"
                + "focusWidth: 0;"
                + "arc: 999;");
        
        glassPanel.putClientProperty("FlatLaf.style", ""
                + "arc: 25;"                              
                + "background: rgba(255, 255, 255, 255);");                     

        jTextField1.setOpaque(false);
        jTextField1.putClientProperty("FlatLaf.style", ""
                + "arc: 15;"
                + "background: rgba(255, 255, 255, 255);"
                + "borderWidth: 0;"
                + "focusWidth: 0;");
        
        // Open university photo menu when clicking label
        jLabel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UniversityViews imageMenu = new UniversityViews();
                imageMenu.setVisible(true);
                MapFrame.this.dispose();
            }
        });

        this.setTitle("Land Mapping System");
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());

        jPanel1.setPreferredSize(new Dimension(300, 0));
        jPanel1.setMinimumSize(new Dimension(300, 0));
        jPanel1.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        jPanel1.setVisible(false);        

        jPanel5.setLayout(new java.awt.GridLayout(1, 3, 10, 0));
        jPanel5.setPreferredSize(new Dimension(this.getWidth(), 50)); 

        this.add(mapPanel, BorderLayout.CENTER); 
        this.add(jPanel1, BorderLayout.EAST);     
        this.add(jPanel5, BorderLayout.SOUTH);    

        JComponent glass = (JComponent) this.getGlassPane();
        glass.setLayout(null); 
        glass.add(jButton2);
        jButton2.setBounds(20, 20, 90, 35); 
        
        glass.add(glassPanel);
        glassPanel.setBounds(145, 12, 434, 50);
        
        glass.setVisible(true);

        initializeMap();
        setupContainerWithImage();

        if (initialImageName != null && !initialImageName.isEmpty()) {
            showImage(initialImageName);
        } else {
            showMap();
        }

        this.revalidate();
        this.repaint();
    }

    private void setupContainerWithImage() {
        containerLayout = new CardLayout();
        mapPanel.setLayout(containerLayout);

        // Custom Interactive Panel instead of a basic JLabel
        interactiveImagePanel = new InteractiveImagePanel(this);

        // Return to Map button
        btnReturnToMap = new JButton("Return to Map");
        btnReturnToMap.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnReturnToMap.setBackground(new Color(231, 76, 60));
        btnReturnToMap.setForeground(Color.WHITE);
        btnReturnToMap.setFocusPainted(false);
        btnReturnToMap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReturnToMap.addActionListener(e -> showMap());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnReturnToMap);

        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.add(buttonPanel, BorderLayout.NORTH);
        imageContainer.add(interactiveImagePanel, BorderLayout.CENTER);

        // Register views
        mapPanel.add(mapViewer, "MAP_VIEW");
        mapPanel.add(imageContainer, "IMAGE_VIEW");
    }

    public void showImage(String imageName) {
        ImageIcon icon = loadImageIcon(imageName);
        if (icon != null) {
            interactiveImagePanel.setImage(icon.getImage());
        }
        containerLayout.show(mapPanel, "IMAGE_VIEW");
    }

    public void showMap() {
        containerLayout.show(mapPanel, "MAP_VIEW");
    }

    private ImageIcon loadImageIcon(String fileName) {
        java.net.URL resource = getClass().getResource("/images/" + fileName);
        if (resource != null) return new ImageIcon(resource);

        resource = getClass().getResource("/" + fileName);
        if (resource != null) return new ImageIcon(resource);

        File projectDir = new File(System.getProperty("user.dir"));
        File foundFile = searchFileRecursive(projectDir, fileName);

        if (foundFile != null && foundFile.exists()) {
            return new ImageIcon(foundFile.getAbsolutePath());
        }
        return null;
    }

    private File searchFileRecursive(File directory, String fileName) {
        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        File result = searchFileRecursive(file, fileName);
                        if (result != null) return result;
                    } else if (file.getName().equalsIgnoreCase(fileName)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    private void initializeMap() {
        mapViewer = new JXMapViewer();

        TileFactoryInfo info = new TileFactoryInfo(1, 19, 19, 256, true, true, 
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile", "x", "y", "z") {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 19 - zoom;
                return this.baseURL + "/" + z + "/" + y + "/" + x;
            }
        };

        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setAddressLocation(new GeoPosition(21.814398, -102.771391));
        mapViewer.setZoom(2);

        plantingPoints = new ArrayList<>();

        Painter<JXMapViewer> areaPainter = (g, map, w, h) -> {
            if (plantingPoints.size() < 2) return;
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            g.translate(-rect.x, -rect.y);
            
            Path2D.Double path = new Path2D.Double();
            boolean first = true;
            for (Waypoint wp : plantingPoints) {
                Point2D pt = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                if (first) { path.moveTo(pt.getX(), pt.getY()); first = false; }
                else { path.lineTo(pt.getX(), pt.getY()); }
            }
            path.closePath();
            g.setColor(new Color(46, 204, 113, 80));
            g.fill(path);
            g.setColor(new Color(39, 174, 96, 220));
            g.setStroke(new BasicStroke(2.5f));
            g.draw(path);
            g.dispose();
        };

        Painter<JXMapViewer> nodePainter = (g, map, w, h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            g.translate(-rect.x, -rect.y);
            int idx = 0;
            for (Waypoint wp : plantingPoints) {
                Point2D pt = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                g.setColor(new Color(41, 128, 185));
                g.fillOval((int)pt.getX() - 8, (int)pt.getY() - 8, 16, 16);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf((char)('A' + (idx++ % 26))), (int)pt.getX() + 12, (int)pt.getY() + 5);
            }
            g.dispose();
        };

        mapViewer.setOverlayPainter(new CompoundPainter<>(areaPainter, nodePainter));

        panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
                    GeoPosition pos = mapViewer.convertPointToGeoPosition(e.getPoint());
                    draggedPoint = findNearbyPoint(pos);
                    if (draggedPoint != null) {
                        mapViewer.removeMouseListener(panListener);
                        mapViewer.removeMouseMotionListener(panListener);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPoint != null) {
                    int index = plantingPoints.indexOf(draggedPoint);
                    draggedPoint = new DefaultWaypoint(mapViewer.convertPointToGeoPosition(e.getPoint()));
                    plantingPoints.set(index, draggedPoint);
                    mapViewer.repaint();
                    updateCalculations();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedPoint != null) {
                    mapViewer.addMouseListener(panListener);
                    mapViewer.addMouseMotionListener(panListener);
                    updateWeatherApi(draggedPoint.getPosition());
                    draggedPoint = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    plantingPoints.add(new DefaultWaypoint(mapViewer.convertPointToGeoPosition(e.getPoint())));
                    if (!plantingPoints.isEmpty()) { 
                        updateWeatherApi(plantingPoints.get(plantingPoints.size() - 1).getPosition()); 
                    }
                } 
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!plantingPoints.isEmpty()) {
                        plantingPoints.remove(plantingPoints.size() - 1);
                        if (!plantingPoints.isEmpty()) { 
                            updateWeatherApi(plantingPoints.get(plantingPoints.size() - 1).getPosition()); 
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
                    plantingPoints.clear();
                    jLabel5.setText("--"); 
                    jLabel3.setText("--"); 
                }
                mapViewer.repaint();
                updateCalculations();
            }
        };

        mapViewer.addMouseListener(mouseAdapter);
        mapViewer.addMouseMotionListener(mouseAdapter);
    }

    private void updateCalculations() {
        if (plantingPoints.size() < 2) {
            jLabel8.setText("0.0");
            jLabel13.setText("0.0");
            return;
        }

        int n = plantingPoints.size();
        double[] lats = new double[n];
        double[] lons = new double[n];

        for (int i = 0; i < n; i++) {
            GeoPosition pos = plantingPoints.get(i).getPosition();
            lats[i] = pos.getLatitude();
            lons[i] = pos.getLongitude();
        }

        Calculation calc = new Calculation("Current Land", lats, lons);
        double area = calc.calculateIntegralArea();
        jLabel8.setText(String.format(java.util.Locale.US, "%.2f", area));

        double perimeterMeters = 0.0;
        double metersPerDegree = 111320.0;

        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            double dLat = lats[next] - lats[i];
            double dLon = lons[next] - lons[i];
            double metersLat = dLat * metersPerDegree;
            double metersLon = dLon * metersPerDegree;
            perimeterMeters += Math.sqrt((metersLat * metersLat) + (metersLon * metersLon));
        }

        jLabel13.setText(String.format(java.util.Locale.US, "%.2f", perimeterMeters));
    }

    private Waypoint findNearbyPoint(GeoPosition pos) {
        for (Waypoint wp : plantingPoints) {
            Point2D p1 = mapViewer.getTileFactory().geoToPixel(pos, mapViewer.getZoom());
            Point2D p2 = mapViewer.getTileFactory().geoToPixel(wp.getPosition(), mapViewer.getZoom());
            if (p1.distance(p2) < 20) return wp;
        }
        return null;
    }

    public void updateWeatherApi(GeoPosition pos) {
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
    
    public void updateImageMetrics(double areaM2, double perimeterM) {
        if (jLabel8 != null) {
            jLabel8.setText(String.format(java.util.Locale.US, "%.1f", areaM2));
        }
        if (jLabel13 != null) {
            jLabel13.setText(String.format(java.util.Locale.US, "%.1f", perimeterM));
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapPanel = new javax.swing.JPanel();
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
        jPanel5 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        glassPanel = new javax.swing.JPanel();
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
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Humedad: ");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("--");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Temperatura:");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("--");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("________________________________");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Área:");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("0.0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setForeground(new java.awt.Color(189, 189, 189));
        jLabel9.setText("________________________________");

        jLabel10.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("%");

        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("°C");

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("m²");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("0.0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setForeground(new java.awt.Color(189, 189, 189));
        jLabel14.setText("________________________________");

        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("m");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
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
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(173, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
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

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(170, 114, 41));
        jButton1.setText("Origen (UTC)");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked1(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(83, 83, 83))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        glassPanel.setBackground(new java.awt.Color(255, 255, 255));
        glassPanel.setName("glassPanel"); // NOI18N

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setForeground(new java.awt.Color(102, 102, 102));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField1.setText("Ingresa el nombre de terreno...");
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(107, 142, 35));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Guardar");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton4MouseExited(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(178, 34, 34));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Limpiar");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton6MouseExited(evt);
            }
        });
        jButton6.addActionListener(this::jButton6ActionPerformed);

        javax.swing.GroupLayout glassPanelLayout = new javax.swing.GroupLayout(glassPanel);
        glassPanel.setLayout(glassPanelLayout);
        glassPanelLayout.setHorizontalGroup(
            glassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(glassPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addContainerGap(7, Short.MAX_VALUE))
        );
        glassPanelLayout.setVerticalGroup(
            glassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, glassPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(glassPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jButton4)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(26, 26, 26)
                .addComponent(glassPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addGroup(mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mapPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(mapPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(glassPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        glassPanel.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        // Invert the current visibility state of the panel
        boolean currentState = jPanel1.isVisible();
        jPanel1.setVisible(!currentState);

        // CRUCIAL: Notify the layout that the screen space has changed
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

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        // TODO add your handling code here:
        jButton1.setBackground(Color.decode("#F2E6D8"));
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        // TODO add your handling code here:
        jButton1.setBackground(Color.decode("#FFFFFF"));
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked1(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked1
       // Open the UniversityViews window asynchronously on the Event Dispatch Thread
        java.awt.EventQueue.invokeLater(() -> {
            new UniversityViews().setVisible(true);
        });

        // Close the current window to free up system resources
        this.dispose();
    }//GEN-LAST:event_jButton1MouseClicked1

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
        // TODO add your handling code here:
        jTextField1.setText("");
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        //INPUT VALIDATION
        String landName = jTextField1.getText().trim();

        if (landName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the name of the land.",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (plantingPoints == null || plantingPoints.size() < 3) {
            JOptionPane.showMessageDialog(this,
                "You must place at least three points on the map to delimit the land area.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return; 
        }

        // Check Checkbox states
        boolean isCarrot = chkCarrot.isSelected();
        boolean isRadish = chkRadish.isSelected();
        boolean isTomato = chkTomato.isSelected();
        boolean isCorn   = chkCorn.isSelected();
        boolean isBean   = chkBean.isSelected();

        

        // BUILD DESCRIPTION FROM ACTIVE CHECKBOXES
        StringBuilder descriptionBuilder = new StringBuilder();
        String landDescription = descriptionBuilder.toString();
        
        if (isCarrot) descriptionBuilder.append("Carrot, ");
        if (isRadish) descriptionBuilder.append("Radish, ");
        if (isTomato) descriptionBuilder.append("Tomato, ");
        if (isCorn)   descriptionBuilder.append("Corn, ");
        if (isBean)   descriptionBuilder.append("Bean, ");
        if (landDescription.isEmpty()) {
            descriptionBuilder.append(" "); 
        }
        // Remove trailing comma and space
        
        if (landDescription.endsWith(", ")) {
            landDescription = landDescription.substring(0, landDescription.length() - 2);
        }
        
        
        //EXTRACT NUMERIC VALUES
        String areaStr = jLabel8.getText().trim();
        String perimeterStr = jLabel13.getText().trim();
        String humidityStr = jLabel3.getText().trim();
        String temperatureStr = jLabel5.getText().trim();

        double area = 0.0;
        double perimeter = 0.0;
        int humidity = 0;
        double temperature = 0.0;

        try {
            if (!areaStr.contains("--") && !areaStr.isEmpty()) {
                area = Double.parseDouble(areaStr);
            }
            if (!perimeterStr.contains("--") && !perimeterStr.isEmpty()) {
                perimeter = Double.parseDouble(perimeterStr);
            }
            if (!humidityStr.contains("--") && !humidityStr.isEmpty()) {
                humidity = Integer.parseInt(humidityStr);
            }
            if (!temperatureStr.contains("--") && !temperatureStr.isEmpty()) {
                temperature = Double.parseDouble(temperatureStr);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing numeric values: " + e.getMessage());
        }

        //REGISTER IN DB PASSING THE GENERATED DESCRIPTION
        int currentUserId = Query.getActiveUserId();
        int currentLayerId = 1; 

        java.time.LocalDate currentDate = java.time.LocalDate.now();
        String formattedDate = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Save 'landDescription' in the 'description' field of 'map'
        int createdMapId = Query.registerMap(landName, landDescription, formattedDate, currentUserId);

        if (createdMapId != -1) {
            Query.registerGeographicElement(formattedDate, area, perimeter, humidity, temperature, currentLayerId, createdMapId);

            //NOTIFICATION AND SCREEN RESET
            JOptionPane.showMessageDialog(this,
                "Land '" + landName + "' saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            if (plantingPoints != null) {
                plantingPoints.clear();
            }
            jTextField1.setText("");
            jLabel3.setText("--");
            jLabel5.setText("--");
            jLabel8.setText("0.0");
            jLabel13.setText("0.0");

            chkCarrot.setSelected(false);
            chkRadish.setSelected(false);
            chkTomato.setSelected(false);
            chkCorn.setSelected(false);
            chkBean.setSelected(false);

            if (jTable1 != null && jTable1.getModel() != null) {
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    jTable1.setValueAt(false, i, 0);
                }
            }

            if (mapViewer != null) {
                mapViewer.repaint();
            }

        } else {
            JOptionPane.showMessageDialog(this,
                "Error saving land to the database.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
        //Clear the list of points
        if (plantingPoints != null) {
            plantingPoints.clear();
        }

        //Clear the land name text field
        jTextField1.setText("Ingresa el nombre de terreno...");

        //Reset the Area and Perimeter labels
        jLabel3.setText("--");
        jLabel5.setText("--");
        jLabel8.setText("0.0");
        jLabel13.setText("0.0");

        //Repaint/refresh the map to clear lines and markers
        if (mapViewer != null) {
            mapViewer.repaint();
        }
        
        chkCarrot.setSelected(false);
        chkRadish.setSelected(false);
        chkTomato.setSelected(false);
        chkCorn.setSelected(false);
        chkBean.setSelected(false);
    }//GEN-LAST:event_jButton6MouseClicked

    private void jButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseEntered
        // TODO add your handling code here:
        jButton4.setBackground(Color.decode("#ACC774"));
    }//GEN-LAST:event_jButton4MouseEntered

    private void jButton4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseExited
        // TODO add your handling code here:
        jButton4.setBackground(Color.decode("#6B8E23"));
    }//GEN-LAST:event_jButton4MouseExited

    private void jButton6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseEntered
        // TODO add your handling code here:
        jButton6.setBackground(Color.decode("#FF3F3F"));
    }//GEN-LAST:event_jButton6MouseEntered

    private void jButton6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseExited
        // TODO add your handling code here:
        jButton6.setBackground(Color.decode("#B22222"));
    }//GEN-LAST:event_jButton6MouseExited
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel glassPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel mapPanel;
    // End of variables declaration//GEN-END:variables
}