/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mapping_integrating_project;

import com.formdev.flatlaf.FlatDarkLaf; // Or FlatLightLaf

/**
 *
 * @author axelr
 */
public class MappingIntegratingProject {

    public static void main(String[] args) {
        
        FlatDarkLaf.setup();
    
        java.awt.EventQueue.invokeLater(() -> {
            new SessionFrom().setVisible(true);
        });
    }
}
