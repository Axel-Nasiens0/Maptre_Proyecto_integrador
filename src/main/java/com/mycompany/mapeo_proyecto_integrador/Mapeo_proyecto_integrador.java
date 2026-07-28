/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mapeo_proyecto_integrador;

import com.formdev.flatlaf.FlatDarkLaf; // O FlatLightLaf

/**
 *
 * @author axelr
 */
public class Mapeo_proyecto_integrador {

    public static void main(String[] args) {
        
        FlatDarkLaf.setup(); // <--- IMPORTANTE: Activa FlatLaf
    
        java.awt.EventQueue.invokeLater(() -> {
            new mapa().setVisible(true);
        });
    }
}
