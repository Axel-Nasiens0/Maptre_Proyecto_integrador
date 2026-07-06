/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/mapeo_proyecto";
    private static final String USER = "root";
    private static final String CONTRASENA = ""; // Asegúrate de colocar tu contraseña real aquí
    
    private static Connection conexionUnica;

    private Conexion() {} 

    public static synchronized Connection conectar() {
        try {
            // Forzar la carga en memoria del Driver de MySQL (Técnica requerida en auditoría de QA)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (conexionUnica == null || conexionUnica.isClosed()) {
                conexionUnica = DriverManager.getConnection(URL, USER, CONTRASENA);
                System.out.println("Conexión Singleton exitosa a Map.tre");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró el Driver de MySQL en las dependencias Maven.");
        } catch (SQLException e) {
            System.out.println("ERROR de base de datos (Verifica credenciales o si el servidor está encendido): " + e.getMessage());
        }
        return conexionUnica;
    }
}