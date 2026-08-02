/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 *
 * @author nana2
 */
public class JLabelRounded extends JLabel{
    private int cornerradius = 60; // Controla qué tan redondas son las esquinas

    public JLabelRounded() {
        // Es crucial que sea falso para que el fondo rectangular por defecto no tape las esquinas
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Activa el suavizado de bordes (Antialiasing) para que no se vea pixelado
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Pinta el fondo redondeado con el color que le hayas asignado al Label
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerradius, cornerradius);
        
        g2.dispose();
        
        // Deja que Java dibuje el texto e iconos encima del fondo que ya pintamos
        super.paintComponent(g);
    }
    
    // Por si quieres cambiar el radio desde el código
    public void setRadioEsquina(int radio) {
        this.cornerradius = radio;
        repaint();
    }
}
