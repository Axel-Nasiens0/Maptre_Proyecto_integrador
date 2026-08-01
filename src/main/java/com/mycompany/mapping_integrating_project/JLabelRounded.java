/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 *
 * @author nana2
 */
public class JLabelRounded extends JLabel {
    private int cornerRadius = 60; // Controls how rounded the corners are

    public JLabelRounded() {
        // Essential to set to false so the default rectangular background doesn't cover the corners
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Enable anti-aliasing so the edges render smoothly without pixelation
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint the rounded background using the label's assigned background color
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
        
        // Let Swing draw the text and icons on top of the rendered background
        super.paintComponent(g);
    }
    
    // Setter method to dynamically update the corner radius from code
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}