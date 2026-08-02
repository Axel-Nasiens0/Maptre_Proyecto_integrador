/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.awt.*;
import javax.swing.JPanel;

/**
 * 
 * @author axelr
 */
public class InteractiveImagePanel extends JPanel {
    private Image currentImage;
    private final MapFrame parentFrame;

    // Internal rendering variables for image placement
    private double scale = 1.0;
    private int imgX = 0;
    private int imgY = 0;

    public InteractiveImagePanel(MapFrame parent) {
        this.parentFrame = parent;
        setBackground(Color.BLACK);
    }

    /**
     * Loads and displays the specified image inside the panel.
     * @param img The image to display
     * @param imageName The name of the image file
     */
    public void setImage(Image img, String imageName) {
        this.currentImage = img;
        repaint();
    }

    // Overload for compatibility if only the image is provided without a name
    public void setImage(Image img) {
        setImage(img, "");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentImage == null) return;

        int imgW = currentImage.getWidth(this);
        int imgH = currentImage.getHeight(this);

        if (imgW <= 0 || imgH <= 0) return;

        // Aspect ratio scaling calculation (Letterbox)
        double scaleX = (double) getWidth() / imgW;
        double scaleY = (double) getHeight() / imgH;
        scale = Math.min(scaleX, scaleY);

        int drawWidth = (int) (imgW * scale);
        int drawHeight = (int) (imgH * scale);

        // Calculate offsets to keep image centered in the panel
        imgX = (getWidth() - drawWidth) / 2;
        imgY = (getHeight() - drawHeight) / 2;

        // Draw centered image
        g2.drawImage(currentImage, imgX, imgY, drawWidth, drawHeight, this);
    }
}