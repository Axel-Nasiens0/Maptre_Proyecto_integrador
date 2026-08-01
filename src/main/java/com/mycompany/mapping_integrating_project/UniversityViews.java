/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * 
 * @author axelr
 */

public class UniversityViews extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UniversityViews.class.getName());

    /**
     * Creates new form uniCalcs
     */
    public UniversityViews() {
        initComponents();
        setupCardsMenu(); // Builds and displays the cards layout
    }
    
    private void setupCardsMenu() {
        setTitle("Dashboard Menu");

        // Main background panel with vertical gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(16, 122, 87);
                Color endColor = Color.WHITE;
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, 350, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 20));

        // Add photo cards for all 9 altitude levels
        mainPanel.add(createCardPanel("Image1.jpeg", "Building H Entrance (~10m)", "Open"));
        mainPanel.add(createCardPanel("Image2.jpeg", "Inner Courtyard (~25m)", "Open"));
        mainPanel.add(createCardPanel("Image3.jpeg", "Building Complex (~50m)", "Open"));
        mainPanel.add(createCardPanel("Image4.jpeg", "Parking & Sports Zone (~80m)", "Open"));
        mainPanel.add(createCardPanel("Image5.jpeg", "Campus Sector (~120m)", "Open"));
        mainPanel.add(createCardPanel("Image6.jpeg", "Macrozone View (~200m)", "Open"));
        mainPanel.add(createCardPanel("Image7.jpeg", "Full Campus Area (~350m)", "Open"));
        mainPanel.add(createCardPanel("Image8.jpeg", "Regional View (~500m)", "Open"));
        mainPanel.add(createCardPanel("Image9 complete.jpeg", "Satellite View (~1000m)", "Open"));

        int cardCount = 9; 
        int rows = (int) Math.ceil((double) cardCount / 5);
        int totalHeight = (rows * 210) + ((rows + 1) * 20);
        mainPanel.setPreferredSize(new Dimension(800, totalHeight));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setSize(840, 520);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Factory method to build individual card component.
     */
    private JPanel createCardPanel(String imageName, String descText, String buttonText) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(140, 210));
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));

        // Center Panel (Image + Text)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = findImageFile(imageName);
        if (icon != null && icon.getIconWidth() > 0) {
            Image scaledImage = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText("[ Not Found ]");
        }

        JTextArea descArea = new JTextArea(descText);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setForeground(Color.DARK_GRAY);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setBackground(Color.WHITE);

        centerPanel.add(imageLabel, BorderLayout.CENTER);
        centerPanel.add(descArea, BorderLayout.SOUTH);

        // Action Button
        JButton actionButton = new JButton(buttonText);
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        actionButton.setForeground(new Color(255, 255, 255));
        actionButton.setBackground(new Color(16, 122, 87));
        actionButton.setBorderPainted(false);
        actionButton.setFocusPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Event listeners: Open map window with selected image
        actionButton.addActionListener(e -> selectAndOpenMap(imageName));
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAndOpenMap(imageName);
            }
        });

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(actionButton, BorderLayout.SOUTH);

        return card;
    }

    private void selectAndOpenMap(String imageName) {
        MapFrame mapFrame = new MapFrame(imageName);
        mapFrame.setVisible(true);
        this.dispose();
    }

    private ImageIcon findImageFile(String fileName) {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 624, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
